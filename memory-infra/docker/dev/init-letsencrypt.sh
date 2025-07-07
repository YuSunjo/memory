#!/bin/bash

# .env 파일에서 환경변수 로드
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

# Memory API 프로젝트 Let's Encrypt 설정
domains=(dev.api.mymemory.co.kr)
rsa_key_size=4096
data_path="./data/certbot"
email="${LETSENCRYPT_EMAIL:-}"
staging=0 # Set to 1 if you're testing your setup to avoid hitting request limits

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Docker Compose 명령어 확인
if docker compose version &> /dev/null; then
    DOCKER_COMPOSE="docker compose"
elif command -v docker-compose &> /dev/null && docker-compose --version &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
else
    echo -e "${RED}ERROR: Docker Compose not found or corrupted!${NC}"
    echo -e "${YELLOW}Please reinstall Docker Compose${NC}"
    exit 1
fi

echo -e "${GREEN}=== Memory API HTTPS Setup with Let's Encrypt ===${NC}"
echo -e "${YELLOW}Domain: ${domains[0]}${NC}"
echo -e "${YELLOW}Email: $email${NC}"
echo -e "${YELLOW}Using Docker Compose: $DOCKER_COMPOSE${NC}"

# 이메일 확인
if [ -z "$email" ]; then
    echo -e "${RED}ERROR: Email address is required!${NC}"
    echo -e "${YELLOW}Set LETSENCRYPT_EMAIL in .env file or as environment variable:${NC}"
    echo -e "${YELLOW}  Option 1: Add 'LETSENCRYPT_EMAIL=your@email.com' to .env file${NC}"
    echo -e "${YELLOW}  Option 2: Run 'LETSENCRYPT_EMAIL=your@email.com ./init-letsencrypt.sh'${NC}"
    exit 1
fi

# 기존 데이터 확인
if [ -d "$data_path" ]; then
  read -p "Existing data found for ${domains[0]}. Continue and replace existing certificate? (y/N) " decision
  if [ "$decision" != "Y" ] && [ "$decision" != "y" ]; then
    exit
  fi
fi

# TLS 매개변수 다운로드
if [ ! -e "$data_path/conf/options-ssl-nginx.conf" ] || [ ! -e "$data_path/conf/ssl-dhparams.pem" ]; then
  echo -e "${GREEN}### Downloading recommended TLS parameters ...${NC}"
  mkdir -p "$data_path/conf"
  curl -s https://raw.githubusercontent.com/certbot/certbot/master/certbot-nginx/certbot_nginx/_internal/tls_configs/options-ssl-nginx.conf > "$data_path/conf/options-ssl-nginx.conf"
  curl -s https://raw.githubusercontent.com/certbot/certbot/master/certbot/certbot/ssl-dhparams.pem > "$data_path/conf/ssl-dhparams.pem"
  echo
fi

# 더미 인증서 생성
echo -e "${GREEN}### Creating dummy certificate for ${domains[0]} ...${NC}"
path="/etc/letsencrypt/live/${domains[0]}"
mkdir -p "$data_path/conf/live/${domains[0]}"
$DOCKER_COMPOSE run --rm --entrypoint "\
  openssl req -x509 -nodes -newkey rsa:$rsa_key_size -days 1\
    -keyout '$path/privkey.pem' \
    -out '$path/fullchain.pem' \
    -subj '/CN=localhost'" certbot
echo

# nginx 시작
echo -e "${GREEN}### Starting nginx ...${NC}"
$DOCKER_COMPOSE up --force-recreate -d nginx
echo

# nginx가 시작될 때까지 대기
echo -e "${YELLOW}Waiting for nginx to start...${NC}"
sleep 10

# 더미 인증서 삭제
echo -e "${GREEN}### Deleting dummy certificate for ${domains[0]} ...${NC}"
$DOCKER_COMPOSE run --rm --entrypoint "\
  rm -Rf /etc/letsencrypt/live/${domains[0]} && \
  rm -Rf /etc/letsencrypt/archive/${domains[0]} && \
  rm -Rf /etc/letsencrypt/renewal/${domains[0]}.conf" certbot
echo

# 실제 인증서 요청
echo -e "${GREEN}### Requesting Let's Encrypt certificate for ${domains[0]} ...${NC}"
# 도메인 매개변수 조합
domain_args=""
for domain in "${domains[@]}"; do
  domain_args="$domain_args -d $domain"
done

# 이메일 인수 선택
case "$email" in
  "") email_arg="--register-unsafely-without-email" ;;
  *) email_arg="--email $email" ;;
esac

# 스테이징 모드 활성화 여부
if [ $staging != "0" ]; then staging_arg="--staging"; fi

$DOCKER_COMPOSE run --rm --entrypoint "\
  certbot certonly --webroot -w /var/www/certbot \
    $staging_arg \
    $email_arg \
    $domain_args \
    --rsa-key-size $rsa_key_size \
    --agree-tos \
    --force-renewal" certbot

if [ $? -eq 0 ]; then
    echo -e "${GREEN}### Certificate successfully obtained!${NC}"
else
    echo -e "${RED}### Certificate request failed!${NC}"
    echo -e "${YELLOW}Check DNS settings and firewall (ports 80/443)${NC}"
    exit 1
fi

echo

# nginx 재로드
echo -e "${GREEN}### Reloading nginx ...${NC}"
$DOCKER_COMPOSE exec nginx nginx -s reload

if [ $? -eq 0 ]; then
    echo -e "${GREEN}=== Setup completed successfully! ===${NC}"
    echo -e "${GREEN}Your Memory API is now available at: https://${domains[0]}${NC}"
    echo -e "${YELLOW}Test endpoints:${NC}"
    echo -e "  - https://${domains[0]}/"
    echo -e "  - https://${domains[0]}/health"
    echo -e "  - https://${domains[0]}/api/..."
else
    echo -e "${RED}### Nginx reload failed!${NC}"
    exit 1
fi
