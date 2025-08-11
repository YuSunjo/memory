#!/bin/bash
# 애플리케이션 헬스체크
for i in {1..30}; do
    if curl -s http://localhost:8080/health; then
        echo "Application is running"
        exit 0
    fi
    echo "Waiting for application to start..."
    sleep 10
done
echo "Application failed to start"
exit 1