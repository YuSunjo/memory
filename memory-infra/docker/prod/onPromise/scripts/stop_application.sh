#!/bin/bash

# 실행 중인 도커 컨테이너 중지
cd /home/ubuntu/my-memory
docker compose down || true

sleep 10

# 기존 컨테이너와 이미지 제거
docker rmi 228749872437.dkr.ecr.ap-northeast-2.amazonaws.com/my_memory/memory:latest || true