# 선착순 티켓 발급 API 백엔드

> 대규모 동시 요청 환경에서  
> **선착순 티켓 발급의 정확성과 무결성**을 보장하기 위한  
> 백엔드 시스템을 구현한 프로젝트입니다.

## 1. 프로젝트 개요
### 1-1. 프로젝트 소개
- 선착순 티켓 발급을 위한 백엔드 시스템
- 육각형 아키텍처(Hexagonal Architecture) 스타일
- 요구사항
  - 선착순으로 티켓 발급
  - 중복 발급 X
  - 단일 서버가 아닌 **멀티 서버 환경을 가정**하여 설계
  - 단순 CRUD가 아닌 **동시성 제어 자체가 핵심 문제**

<img width="400" alt="image" src="https://user-images.githubusercontent.com/80039556/220270455-eb158309-37ed-44f0-92c6-83c94e121dab.png">

### 1-2. 문제 해결 전략

초기에는 DB의 Exclusive Lock을 통해 동시성 문제를 해결할 수 있다고 판단했습니다.

하지만 이는
- 티켓 수량 테이블이 **병목 지점(Single Hot Spot)** 이 되고
- 다른 티켓 조회/사용 로직까지 대기하게 되는 문제
- 데드락 가능성 증가

라는 한계를 가졌습니다.

따라서 **락의 범위를 DB → Redis로 이동**시켜  
DB는 결과 저장소로만 사용하고,  
동시성 제어는 Redis 분산락으로 처리하는 구조를 선택했습니다.

**`CreateMemberCouponService`의 임계 구역(Critical Section) 처리 순서:**

1. Redis 분산락 획득 (`COUPON` 락, 대기 10초, 유지 3초)
2. `coupon.remainQuantity > 0` 재고 확인
3. 해당 회원의 중복 발급 여부 확인
4. DB에서 `remainQuantity` 감소
5. `MemberCoupon` 레코드 저장
6. 락 해제 (`finally` 블록에서 보장)

### 1-3. 기술 스택

- Language : `Java 11`
- Framework : `Spring Boot 2.7.8`
- Database : `MySQL 8.0`, `JPA`, `QueryDSL`, `Redis`
- API Documentation : `Swagger 3.0.0`
- 분산락 : `Redisson 3.17.0`

<br>

### 1-4. 실행 환경

**필수 인프라**
- MySQL: `localhost:3308`, 데이터베이스 `coupon_fifo`
- Redis: `localhost:6379`

> JPA DDL이 `create`로 설정되어 있어 애플리케이션 시작 시 스키마가 자동 생성됩니다.

**인프라 실행 (Docker Compose)**
```bash
# MySQL, Redis 컨테이너 실행
docker-compose up -d

# 컨테이너 종료
docker-compose down
```

**실행 명령어**
```bash
# 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 테스트
./gradlew test
```

<br>

## 2. 개발 내용
### 2-1. 백엔드 아키텍처
<img width="798" alt="image" src="https://user-images.githubusercontent.com/80039556/220278524-9dbd7c71-4d01-4fc1-b07e-c44cc7a45718.png">

### 2-2. 육각형 아키텍처 패키지 구조
- 회원티켓 발급 API에 관한 클래스만 적은 패키지 구조

<img width="765" alt="image" src="https://github.com/user-attachments/assets/f5bd56a4-2f02-4dd4-9e2d-14f2db18de16" />

<img width="1100" alt="image" src="https://user-images.githubusercontent.com/80039556/220341229-e8e13aac-0d12-43df-afcd-abc837712e6e.png">

<img width="656" alt="image" src="https://user-images.githubusercontent.com/80039556/220346288-a175bf58-fae0-4a34-95aa-78c9e0e0cc4a.png">

### 2-3. 데이터베이스 ERD

<img width="902" height="789" alt="image" src="https://github.com/user-attachments/assets/b60f972e-58c6-4902-87f1-c2c21c907d0f" />

| 엔티티 | 역할 |
|---|---|
| `TICKET` (`Coupon`) | 발급 대상 티켓 정의 — `totalQuantity` / `remainQuantity` 관리 |
| `MEMBER` | 쿠폰을 발급받을 수 있는 회원 정보 |
| `MEMBER_TICKET` (`MemberCoupon`) | 회원이 발급받은 티켓 이력 — `UseType` (UNUSED/USE) 및 타임스탬프 추적 |

> `MemberCoupon`은 생성 후 불변(immutable)으로 설계되며, 사용 처리는 `use()` 팩토리 메서드를 통해서만 가능합니다.

### 2-4. API 문서화
<img width="1502" height="1167" alt="image" src="https://github.com/user-attachments/assets/01eb7ca8-49c3-4947-8f31-b99e615ac55e" />

### 2-5. 티켓 발급 동시성 테스트

<img width="1023" alt="image" src="https://github.com/Sangyong-Jeon/coupon_fifo-concurrency_issue/assets/80039556/e0c56eaf-11be-4532-905c-194acfda7c50">

**테스트 조건**
- 스레드 15개로 **10,000건의 발급 요청을 동시에** 처리
- 초기 재고: **20,000개**

**검증 항목**
- 발급된 `MemberCoupon` 레코드 = 정확히 **10,000건**
- 남은 재고 `remainQuantity` = **10,000개** (초과 발급 없음)
- 동일 회원 중복 발급 없음

<img width="1317" height="566" alt="image" src="https://github.com/user-attachments/assets/2f077d38-8224-4415-a36e-43d4dbfc3588" />
