#!/bin/bash
# 필요한 디렉토리 생성
mkdir -p /home/ec2-user/my-memory

# Docker가 설치되어 있는지 확인
if ! command -v docker &> /dev/null; then
    sudo yum update -y
    sudo yum install -y docker
fi

# Docker Compose가 설치되어 있는지 확인
if ! command -v docker-compose &> /dev/null; then
    sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
fi

# Docker 서비스 시작 및 사용자 권한 설정
sudo service docker start
sudo systemctl enable docker
sudo usermod -a -G docker ec2-user
sudo chmod 666 /var/run/docker.sock