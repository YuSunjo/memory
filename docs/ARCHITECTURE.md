# 프로젝트 아키텍처

## Domain-Driven Design Architecture
Memory 프로젝트는 Clean Architecture와 DDD 원칙을 따르는 6개 모듈 구조입니다:

**모듈별 역할**:
- **memory-api**: Controllers, Business Services, Request/Response DTOs (Application Layer) 
- **memory-domain**: JPA Entities, Repository Interfaces, Domain Objects (Domain Layer)
- **memory-adapter**: Repository Implementations, External System Integrations (Infrastructure Layer)
- **memory-common**: Cross-cutting concerns (JWT, security, exceptions, annotations)
- **memory-batch**: Spring Batch jobs and scheduling
- **memory-infra**: Docker configurations for different environments

## 전체 아키텍처 구조도

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT (Web/Mobile)                     │
└─────────────────────────┬───────────────────────────────────────┘
                          │ HTTP Request
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                      MEMORY-API MODULE                          │
├─────────────────────────────────────────────────────────────────┤
│  MemberController                                               │
│  ├── POST /api/members/register                                 │
│  └── MemberService (Business Logic)                             │
│      ├── 이메일 중복 검증                                        │
│      ├── 패스워드 정책 검증                                      │
│      └── Member 엔티티 생성/저장                                 │
└─────────────────────────┬───────────────────────────────────────┘
                          │ JPA Repository Call
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                    MEMORY-DOMAIN MODULE                         │
├─────────────────────────────────────────────────────────────────┤
│  Member (JPA Entity)                                           │
│  ├── @Id Long id                                               │
│  ├── @Column String email                                      │
│  ├── @Column String name                                       │
│  └── @Column String password                                   │
│                                                                 │
│  MemberRepository (extends JpaRepository)                      │
│  ├── Optional<Member> findByEmail(String email)                │
│  ├── boolean existsByEmail(String email)                       │
│  └── Custom QueryDSL methods                                   │
│                                                                 │
│  Repository Interfaces Only                                    │
└─────────────────────────┬───────────────────────────────────────┘
                          │ Database Query
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                       DATABASE                                  │
├─────────────────────────────────────────────────────────────────┤
│  PostgreSQL + PostGIS                                          │
│  ├── members 테이블                                             │
│  ├── memories 테이블 (지리정보 포함)                             │
│  └── 기타 도메인 테이블들                                        │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                   MEMORY-ADAPTER MODULE                         │
├─────────────────────────────────────────────────────────────────┤
│  Repository Implementations (JPA, QueryDSL)                    │
│  ├── MemberRepositoryCustomImpl                                │
│  ├── MemoryRepositoryCustomImpl                                │
│  └── External System Adapters                                  │
│      ├── S3 Storage Service                                    │
│      ├── ElasticSearch Integration                             │
│      └── Database Configuration                                │
└─────────────────────────┬───────────────────────────────────────┘
                              │ External Systems
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    MEMORY-COMMON MODULE                         │
├─────────────────────────────────────────────────────────────────┤
│  ├── @Auth, @MemberId 어노테이션                                │
│  ├── JWT 토큰 처리 (JwtComponent)                               │
│  ├── Spring Security 설정                                       │
│  └── 공통 예외 처리                                             │
└─────────────────────────────────────────────────────────────────┘
```

## 의존성 흐름도

```
memory-api ──────────→ memory-domain ←── memory-adapter
    │                      ↑                    ↑
    └─────→ memory-common ──┘────────────────────┘
            ↑
memory-batch ┘
```

## 데이터 플로우 (회원가입 API 호출시)

```
1. Client Request
   POST /api/members/register
   {
     "email": "user@example.com",
     "password": "password123",
     "name": "홍길동"
   }
   ↓

2. API Layer (MemberController)
   - Request 데이터 검증
   - MemberService 호출
   ↓

3. Service Layer (MemberService)
   - 비즈니스 로직 실행
   - MemberRepository를 통한 중복 검증
   - Member 엔티티 생성 및 저장
   ↓

4. Domain Layer (MemberRepository + JPA)
   - JPA를 통한 데이터베이스 연산
   - QueryDSL 활용한 복잡한 쿼리 처리
   ↓

5. Database (PostgreSQL)
   - members 테이블에 데이터 저장
   - 트랜잭션 처리
   ↓

6. Response Flow (역방향)
   Database → JPA Entity → Service → Controller → Client
```

## 개발 순서 (MEMBER 회원가입 API 예시)

1. **Domain 계층**: Member JPA 엔티티, MemberRepository 인터페이스
2. **Adapter 계층**: MemberRepositoryCustomImpl (구현체), 외부 시스템 연동
3. **API 계층**: MemberRequest/Response DTOs, MemberService, MemberController  
4. **데이터베이스**: Flyway 마이그레이션 파일 작성
5. **테스트**: Service 단위 테스트, Controller 통합 테스트

## 핵심 원칙
- **의존성 방향**: API → Domain ← Adapter, 모든 모듈 → Common
- **Domain 순수성**: 순수 JPA 엔티티와 Repository 인터페이스만 포함, 외부 의존성 없음
- **Adapter 패턴**: 외부 시스템 연동 전담 (JPA 구현체, S3, ElasticSearch)
- **DTO 분리**: API 계약용 DTO는 memory-api, 도메인 특화 DTO는 memory-domain
- **테스트 전략**: Domain 단위 테스트 → Service 통합 테스트 → Controller E2E 테스트