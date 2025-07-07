#!/bin/bash

# Memory API 인증서 갱신 스크립트

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Memory API - Renewing Let's Encrypt Certificate ===${NC}"

# 인증서 갱신
echo -e "${YELLOW}Checking and renewing certificates...${NC}"
docker-compose run --rm certbot renew

# nginx 재로드
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Certificate renewal completed successfully!${NC}"
    echo -e "${YELLOW}Reloading nginx...${NC}"
    docker-compose exec nginx nginx -s reload
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Nginx reloaded successfully!${NC}"
        echo -e "${GREEN}Certificate renewal process completed!${NC}"
    else
        echo -e "${RED}Nginx reload failed!${NC}"
        exit 1
    fi
else
    echo -e "${YELLOW}No certificates were renewed (they may not be due for renewal yet)${NC}"
fi

# 인증서 상태 확인
echo -e "${YELLOW}Current certificate status:${NC}"
docker-compose run --rm certbot certificates
