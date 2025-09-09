# 개발 환경 설정

## 사전 요구사항
- Java 17
- Docker & Docker Compose
- Gradle

## 로컬 개발 환경 구축

1. **저장소 클론**:
   ```bash
   git clone https://github.com/yourusername/memory.git
   cd memory
   ```

2. **인프라 환경 시작** (PostgreSQL + PostGIS):
   ```bash
   ./gradlew memory-infra:localStart
   ```

3. **프로젝트 빌드**:
   ```bash
   ./gradlew build
   ```

4. **애플리케이션 실행**:
   ```bash
   ./gradlew memory-api:bootRun
   ```

5. **API 문서 확인**:
   ```
   http://localhost:8080/swagger-ui.html
   ```

## 환경별 Docker 명령어
```bash
# 로컬 환경
./gradlew memory-infra:localStart    # 시작
./gradlew memory-infra:localStop     # 중지

# 개발 환경  
./gradlew memory-infra:devStart      # 시작
./gradlew memory-infra:devStop       # 중지

# 프로덕션 환경
./gradlew memory-infra:prodStart     # 시작
./gradlew memory-infra:prodStop      # 중지

# Docker 리소스 정리
./gradlew memory-infra:cleanDocker
```

## 테스트

### HTTP 클라이언트 파일
`http/` 디렉토리에는 API 테스트를 위한 HTTP 클라이언트 파일들이 도메인별로 정리되어 있습니다:

```
http/
├── member/member.http              # 회원 관련 API 테스트
├── map/map.http                    # 지도 관련 API 테스트  
├── memory/memory.http              # 추억 관련 API 테스트
├── file/file.http                  # 파일 업로드 테스트
├── game/                           # 게임 관련 API 테스트
├── ...
└── http-client.env.json            # 환경 변수 설정
```

IntelliJ IDEA의 HTTP Client나 VS Code의 REST Client 확장으로 사용할 수 있습니다.

### 단위 테스트 실행
```bash
./gradlew test                      # 전체 테스트 실행
./gradlew memory-api:test           # API 모듈 테스트만 실행
./gradlew memory-domain:test        # 도메인 모듈 테스트만 실행
```

## CI/CD

### 개발 서버
`.github/workflows` 디렉토리의 GitHub Actions 워크플로우:

- **build-push.yml**: 애플리케이션 빌드 및 Docker 이미지 푸시
- **deploy-dev.yml**: 개발 환경 자동 배포
- **run-tests.yml**: 자동화된 테스트 실행

### 프로덕션 서버
프로덕션 환경은 AWS PIPELINE을 통해 관리되며, EC2 인스턴스에 배포됩니다.

## 프로젝트 구조

### 전체 디렉토리 구조
```
memory/
├── .github/                        # GitHub 설정
│   └── workflows/                  # CI/CD 파이프라인
├── docs/                           # 프로젝트 문서
├── http/                           # API 테스트 파일
├── memory-api/                     # 🌐 웹 계층
├── memory-common/                  # 🔧 공통 계층
├── memory-domain/                  # 📊 도메인 계층
├── memory-infra/                   # 🐳 인프라 계층
├── memory-batch/                   # ⚡ 배치 계층
├── build.gradle                    # 루트 빌드 설정
├── settings.gradle                 # 모듈 설정
└── README.md                       # 프로젝트 개요
```