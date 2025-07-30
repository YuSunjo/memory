#!/bin/bash

# Elasticsearch TLS 자동 설정 스크립트
set -e

# .env 파일 로드
if [ -f ".env" ]; then
    echo "🔧 .env 파일 로드 중..."
    export $(cat .env | xargs)
    echo "✅ 환경변수 로드 완료 (ELASTIC_PASSWORD=${ELASTIC_PASSWORD:0:3}***)"
else
    echo "❌ .env 파일을 찾을 수 없습니다."
    exit 1
fi

CERTS_DIR="./certs"
ES_CONTAINER="memory-elasticsearch-dev"

echo "🔍 Elasticsearch TLS 상태 확인 중..."

# TLS가 이미 설정되어 있는지 확인
check_tls_enabled() {
    if docker exec $ES_CONTAINER curl -s -k https://localhost:9200/_cluster/health >/dev/null 2>&1; then
        echo "✅ Elasticsearch TLS가 이미 활성화되어 있습니다."
        return 0
    else
        echo "❌ Elasticsearch TLS가 비활성화되어 있습니다."
        return 1
    fi
}

# 인증서 생성
generate_certificates() {
    echo "🔧 Elasticsearch 인증서 생성 중..."
    
    mkdir -p $CERTS_DIR
    
    # 기존 인증서 파일 정리
    docker exec $ES_CONTAINER bash -c "
        cd /usr/share/elasticsearch
        rm -f config/certs/ca.zip config/certs/elasticsearch.zip
        mkdir -p config/certs
    "
    
    # CA 인증서 생성
    docker exec $ES_CONTAINER bash -c "
        cd /usr/share/elasticsearch
        if [ ! -f config/certs/ca/ca.crt ]; then
            bin/elasticsearch-certutil ca --silent --pem -out config/certs/ca.zip
            cd config/certs && unzip -o ca.zip && rm ca.zip
        fi
    "
    
    # Elasticsearch 인증서 생성
    docker exec $ES_CONTAINER bash -c "
        cd /usr/share/elasticsearch
        if [ ! -f config/certs/elasticsearch/elasticsearch.crt ]; then
            bin/elasticsearch-certutil cert --silent --pem \
                --ca-cert config/certs/ca/ca.crt \
                --ca-key config/certs/ca/ca.key \
                --name elasticsearch \
                --dns elasticsearch,localhost \
                --ip 127.0.0.1 \
                -out config/certs/elasticsearch.zip
            cd config/certs && unzip -o elasticsearch.zip && rm elasticsearch.zip
        fi
    "
    
    # 컨테이너 내부에서만 필요한 권한 설정 (소유권 변경 제거)
    docker exec -u root $ES_CONTAINER bash -c "
        chmod -R 755 /usr/share/elasticsearch/config/certs
    "
    
    echo "✅ 인증서 생성 완료"
}

# Elasticsearch 설정 업데이트
update_elasticsearch_config() {
    echo "🔧 Elasticsearch TLS 설정 적용 중..."
    
    # 기존 TLS 설정 제거 후 새로 추가
    docker exec $ES_CONTAINER bash -c "
        # 기존 TLS 관련 설정 제거
        sed -i '/# TLS\/SSL Settings/,\$d' /usr/share/elasticsearch/config/elasticsearch.yml
        
        # 새로운 TLS 설정 추가
        cat >> /usr/share/elasticsearch/config/elasticsearch.yml << 'EOF'

# TLS/SSL Settings
xpack.security.http.ssl.enabled: true
xpack.security.http.ssl.key: certs/elasticsearch/elasticsearch.key
xpack.security.http.ssl.certificate: certs/elasticsearch/elasticsearch.crt
xpack.security.http.ssl.certificate_authorities: certs/ca/ca.crt

xpack.security.transport.ssl.enabled: true
xpack.security.transport.ssl.key: certs/elasticsearch/elasticsearch.key
xpack.security.transport.ssl.certificate: certs/elasticsearch/elasticsearch.crt
xpack.security.transport.ssl.certificate_authorities: certs/ca/ca.crt
xpack.security.transport.ssl.verification_mode: certificate
EOF
    "
    
    echo "✅ Elasticsearch 설정 업데이트 완료"
}

# Spring Boot용 truststore 생성
create_truststore() {
    echo "🔧 Spring Boot용 truststore 생성 중..."
    
    # 컨테이너에서 호스트로 CA 인증서 복사
    echo "📋 CA 인증서를 호스트로 복사 중..."
    docker cp $ES_CONTAINER:/usr/share/elasticsearch/config/certs/ca/ca.crt $CERTS_DIR/
    
    # 복사된 파일 권한 수정
    sudo chown ubuntu:ubuntu $CERTS_DIR/ca.crt
    sudo chmod 644 $CERTS_DIR/ca.crt
    
    # 호스트의 certs 디렉토리에서 CA 인증서 확인
    if [ -f "$CERTS_DIR/ca.crt" ]; then
        echo "CA 인증서 발견: $CERTS_DIR/ca.crt"
    else
        echo "❌ CA 인증서를 찾을 수 없습니다: $CERTS_DIR/ca.crt"
        return 1
    fi

    # 기존 truststore 제거
    rm -f $CERTS_DIR/elasticsearch-truststore.p12

    # PKCS12 truststore 생성 (OpenSSL 사용 - ARM64 호환)
    echo "📦 OpenSSL로 PKCS12 truststore 생성 중..."
    openssl pkcs12 -export -nokeys \
        -in $CERTS_DIR/ca.crt \
        -out $CERTS_DIR/elasticsearch-truststore.p12 \
        -name elasticsearch-ca \
        -passout pass:changeit
    
    # 권한 설정
    chmod 644 $CERTS_DIR/elasticsearch-truststore.p12

    echo "✅ Truststore 생성 완료: $CERTS_DIR/elasticsearch-truststore.p12"
}

# 컨테이너 재시작
restart_containers() {
    echo "🔄 컨테이너 재시작 중..."
    docker compose restart elasticsearch
    
    # Elasticsearch가 시작될 때까지 대기
    echo "⏳ Elasticsearch 시작 대기 중..."
    sleep 30
    
    # HTTPS로 헬스 체크
    for i in {1..30}; do
        if docker exec $ES_CONTAINER curl -s -k -u elastic:${ELASTIC_PASSWORD} https://localhost:9200/_cluster/health >/dev/null 2>&1; then
            echo "✅ Elasticsearch HTTPS 연결 성공!"
            break
        fi
        echo "⏳ Elasticsearch 시작 대기 중... ($i/30)"
        sleep 10
    done
}

# Kibana 사용자 설정
setup_kibana_user() {
    echo "🔧 Kibana 사용자 설정 중..."
    
    # kibana_system 사용자 비밀번호 설정
    for i in {1..5}; do
        echo "🔍 kibana_system 사용자 비밀번호 설정 시도 ($i/5)..."
        
        RESULT=$(docker exec $ES_CONTAINER curl -s -k --connect-timeout 10 --max-time 30 \
            -u elastic:${ELASTIC_PASSWORD} \
            -X POST "https://localhost:9200/_security/user/kibana_system/_password" \
            -H "Content-Type: application/json" \
            -d '{"password": "kibana_password"}' 2>&1)
        
        echo "📋 응답: $RESULT"
        
        if echo "$RESULT" | grep -q '"acknowledged":true'; then
            echo "✅ kibana_system 사용자 비밀번호 설정 완료"
            break
        fi
        
        echo "⏳ kibana_system 사용자 설정 재시도... ($i/5)"
        sleep 5
    done
    
    # 설정 확인
    if docker exec $ES_CONTAINER curl -s -k -u kibana_system:kibana_password "https://localhost:9200/_cluster/health" >/dev/null 2>&1; then
        echo "✅ kibana_system 사용자 인증 확인 완료"
        return 0
    else
        echo "❌ kibana_system 사용자 인증 실패"
        return 1
    fi
}

# 메인 실행 로직
main() {
    # Docker 컨테이너가 실행 중인지 확인
    if ! docker ps | grep -q $ES_CONTAINER; then
        echo "❌ Elasticsearch 컨테이너가 실행되지 않았습니다."
        echo "먼저 'docker compose up -d'를 실행하세요."
        exit 1
    fi
    
    # TLS 상태 확인
    if check_tls_enabled; then
        echo "🎉 TLS 설정이 완료되어 있습니다."
        # Kibana 사용자 설정은 항상 확인
        echo "📍 기존 TLS 환경에서 setup_kibana_user 실행 전"
        setup_kibana_user
        echo "📍 기존 TLS 환경에서 setup_kibana_user 실행 후"
        
        # Kibana 컨테이너 재시작 (HTTPS 설정 적용)
        echo "🔄 Kibana 컨테이너 재시작 중..."
        docker compose restart kibana
        sleep 10
        
        exit 0
    fi
    
    echo "🚀 TLS 설정을 시작합니다..."
    
    # 단계별 실행
    generate_certificates
    update_elasticsearch_config
    create_truststore
    restart_containers
    echo "📍 setup_kibana_user 실행 전"
    setup_kibana_user
    echo "📍 setup_kibana_user 실행 후"
    
    # Kibana 컨테이너 재시작 (HTTPS 설정 적용)
    echo "🔄 Kibana 컨테이너 재시작 중..."
    docker compose restart kibana
    sleep 10
    
    echo "🎉 Elasticsearch TLS 및 Kibana 설정이 완료되었습니다!"
    echo "📋 Kibana 접속: http://localhost:5601"
}

# 스크립트 실행
main "$@"