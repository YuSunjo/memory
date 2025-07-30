#!/bin/bash

# Elasticsearch TLS 자동 설정 스크립트
set -e

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
    
    # CA 인증서 생성
    docker exec $ES_CONTAINER bash -c "
        cd /usr/share/elasticsearch
        if [ ! -f config/certs/ca.crt ]; then
            mkdir -p config/certs
            bin/elasticsearch-certutil ca --silent --pem -out config/certs/ca.zip
            cd config/certs && unzip -o ca.zip
        fi
    "
    
    # Elasticsearch 인증서 생성
    docker exec $ES_CONTAINER bash -c "
        cd /usr/share/elasticsearch
        if [ ! -f config/certs/elasticsearch.crt ]; then
            bin/elasticsearch-certutil cert --silent --pem \
                --ca-cert config/certs/ca/ca.crt \
                --ca-key config/certs/ca/ca.key \
                --name elasticsearch \
                --dns elasticsearch,localhost \
                --ip 127.0.0.1 \
                -out config/certs/elasticsearch.zip
            cd config/certs && unzip -o elasticsearch.zip
            
            # 권한 설정
            chown -R elasticsearch:elasticsearch config/certs
            chmod -R 750 config/certs
        fi
    "
    
    echo "✅ 인증서 생성 완료"
}

# Elasticsearch 설정 업데이트
update_elasticsearch_config() {
    echo "🔧 Elasticsearch TLS 설정 적용 중..."
    
    # elasticsearch.yml 설정 추가
    docker exec $ES_CONTAINER bash -c "
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
    
    # CA 인증서를 호스트로 복사
    docker cp $ES_CONTAINER:/usr/share/elasticsearch/config/certs/ca/ca.crt $CERTS_DIR/
    
    # PKCS12 truststore 생성
    docker run --rm -v $(pwd)/$CERTS_DIR:/certs openjdk:17-alpine \
        keytool -import -trustcacerts -noprompt \
        -alias elasticsearch-ca \
        -file /certs/ca.crt \
        -keystore /certs/elasticsearch-truststore.p12 \
        -storetype PKCS12 \
        -storepass changeit
    
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
        exit 0
    fi
    
    echo "🚀 TLS 설정을 시작합니다..."
    
    # 단계별 실행
    generate_certificates
    update_elasticsearch_config
    create_truststore
    restart_containers
    
    echo "🎉 Elasticsearch TLS 설정이 완료되었습니다!"
    echo "📋 다음 단계:"
    echo "   1. docker-compose.yml에서 SPRING_ELASTICSEARCH_URIS를 https://elasticsearch:9200으로 변경"
    echo "   2. Spring Boot 설정에 truststore 추가"
    echo "   3. docker compose restart app"
}

# 스크립트 실행
main "$@"