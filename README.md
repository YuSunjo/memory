# Memory Project

## 🌟 서비스 소개
Memory는 지도 기반의 추억 공유 사이트로, 사용자들이 특정 위치에 자신의 추억을 기록하고 공유할 수 있는 서비스입니다.

## 🚀 빠른 시작

### 로컬 개발 환경 구축
```bash
# 저장소 클론
git clone https://github.com/yourusername/memory.git
cd memory

# 인프라 환경 시작 (PostgreSQL + PostGIS)
./gradlew memory-infra:localStart

# 프로젝트 빌드 및 실행
./gradlew build
./gradlew memory-api:bootRun

# API 문서 확인
open http://localhost:8080/swagger-ui.html
```

## 📚 문서

자세한 문서는 [docs](./docs/) 폴더에서 확인하실 수 있습니다:

- **[아키텍처](./docs/ARCHITECTURE.md)** - 전체 시스템 아키텍처 및 설계 원칙
- **[멀티모듈 구조](./docs/MODULES.md)** - 6개 모듈의 역할과 구조
- **[기술 스택](./docs/TECH_STACK.md)** - 사용된 기술과 선택 이유
- **[개발 환경](./docs/DEVELOPMENT.md)** - 개발 환경 설정 및 테스트 가이드

## 🏗️ 프로젝트 구조

```
memory/
├── memory-api/         # 🌐 Presentation Layer (REST API)
├── memory-domain/      # 🎯 Domain Layer (Entities, Interfaces)  
├── memory-adapter/     # 🔌 Infrastructure Layer (Implementations)
├── memory-common/      # 🔧 Common Utilities (JWT, Security)
├── memory-batch/       # ⚡ Batch Processing
├── memory-infra/       # 🐳 Infrastructure & DevOps
└── docs/               # 📚 프로젝트 문서
```

## 🛠️ 주요 기술

- **Backend**: Java 21, Spring Boot 3.5.x, Spring Security
- **Database**: PostgreSQL + PostGIS (지리공간 데이터)
- **Infrastructure**: Docker, AWS ECS, GitHub Actions
- **Documentation**: Swagger/OpenAPI

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.