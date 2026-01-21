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

초기에는 DB의 Exclusive Lock을 통해
동시성 문제를 해결할 수 있다고 판단했습니다.

하지만 이는
- 티켓 수량 테이블이 **병목 지점(Single Hot Spot)** 이 되고
- 다른 티켓 조회/사용 로직까지 대기하게 되는 문제
- 데드락 가능성 증가

라는 한계를 가졌습니다.

따라서 **락의 범위를 DB → Redis로 이동**시켜  
DB는 결과 저장소로만 사용하고,  
동시성 제어는 Redis 분산락으로 처리하는 구조를 선택했습니다.

### 1-3. 기술 스택

- Language : `Java 11`
- Framework : `Spring Boot 2.7.8`
- Database : `MySQL 8.0`, `JPA`, `QueryDSL`, `Redis`
- API Documentation : `Swagger 3.0.0`

<br>

## 2. 개발 내용
### 2-1. 백엔드 아키텍처
<img width="798" alt="image" src="https://user-images.githubusercontent.com/80039556/220278524-9dbd7c71-4d01-4fc1-b07e-c44cc7a45718.png">

### 2-2. 육각형 아키텍처 패키지 구조
- 회원티켓 발급 API에 관한 클래스만 적은 패키지 구조
- 
- <img width="765" alt="image" src="https://github.com/user-attachments/assets/f5bd56a4-2f02-4dd4-9e2d-14f2db18de16" />

<img width="1100" alt="image" src="https://user-images.githubusercontent.com/80039556/220341229-e8e13aac-0d12-43df-afcd-abc837712e6e.png">

<img width="656" alt="image" src="https://user-images.githubusercontent.com/80039556/220346288-a175bf58-fae0-4a34-95aa-78c9e0e0cc4a.png">


### 2-3. 데이터베이스 ERD
<img width="902" height="789" alt="image" src="https://github.com/user-attachments/assets/b60f972e-58c6-4902-87f1-c2c21c907d0f" />

- TICKET : 발급 대상 티켓 정보
- MEMBER : 회원 정보
- MEMBER_TICKET : 회원이 발급받은 티켓 이력

### 2-4 API 문서화
<img width="1502" height="1167" alt="image" src="https://github.com/user-attachments/assets/01eb7ca8-49c3-4947-8f31-b99e615ac55e" />

<!-- ### 2-5 부하 테스트

<img width="247" alt="image" src="https://user-images.githubusercontent.com/80039556/220295853-4f4a2d18-d61f-4c53-9a70-98ff4ec6ca0e.png">

- JMeter를 사용하여 5초안에 2000명 요청하는 것을 5번 반복
- 티켓남은개수는 1000개이고, 회원은 10000명이 있음
- MEMBER_COUPON 값 : 뒤죽박죽 요청된 것을 볼 수 있음
<img width="686" alt="image" src="https://user-images.githubusercontent.com/80039556/220296440-e975c9a1-b666-4864-8f84-1fd8091e23a5.png">

- COUPON 값 : 제한된 1000개의 수량만큼만 빠진것을 볼 수 있음
<img width="683" alt="image" src="https://user-images.githubusercontent.com/80039556/220296640-51a3453f-56f7-43b1-b133-63c02d5abf10.png">

- 발급된 회원티켓 개수 : 1000개만 발급된 것을 볼 수 있음
<img width="484" alt="image" src="https://user-images.githubusercontent.com/80039556/220296750-f5547c97-c4a1-4de2-bef1-efb66e67c479.png">

- 회원티켓 테이블에서 중복 발급되었는지 조회 : 중복 발급이 없는 것을 볼 수 있음
  - 11번 티켓에 대해서만 발급요청을 했기에 회원id(mem_id)가 중복됐는지만 체크하면 됨
<img width="269" alt="image" src="https://user-images.githubusercontent.com/80039556/220330483-123f1a72-4ae4-42dc-8210-08205191e6b1.png">  -->

### 2-6 티켓발급 테스트
<img width="1023" alt="image" src="https://github.com/Sangyong-Jeon/coupon_fifo-concurrency_issue/assets/80039556/e0c56eaf-11be-4532-905c-194acfda7c50">

- 15개 쓰레드 생성하여 10000명의 회원티켓 발급을 병렬처리 테스트함
