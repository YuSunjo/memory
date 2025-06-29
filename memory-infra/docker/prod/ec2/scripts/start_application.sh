#!/bin/bash
# AWS ECR 로그인
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 228749872437.dkr.ecr.ap-northeast-2.amazonaws.com

cd /home/ec2-user/my-memory

# 기존 컨테이너와 이미지 제거
docker-compose down
docker rmi 228749872437.dkr.ecr.ap-northeast-2.amazonaws.com/my_memory/memory:latest

# 새 이미지 가져오기 및 실행
docker-compose pull
docker-compose up -d
