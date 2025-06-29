#!/bin/bash

# 실행 중인 도커 컨테이너 중지
cd /home/ec2-user/my-memory
docker-compose down || true
