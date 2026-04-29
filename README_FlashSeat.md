# FlashSeat

선착순 좌석 예매 및 정산 기능을 구현한 백엔드 중심 개인 프로젝트입니다.  
단순 CRUD를 넘어서 **예매 트랜잭션 정합성**, **동일 좌석 동시 요청 제어**, **미결제 만료 배치**, **조회 성능 개선**, **일별 정산 배치**를 끝까지 구현하고 검증하는 것을 목표로 했습니다.

## 1. 프로젝트 소개

FlashSeat는 인기 행사 예매 상황을 가정한 좌석형 티켓팅 시스템입니다.  
사용자는 회원가입 및 로그인 후 행사/회차/좌석을 조회하고, 특정 좌석을 예매한 뒤 결제 Mock 성공 처리 또는 취소를 수행할 수 있습니다.  
운영 측면에서는 미결제 예매 만료 처리와 일별 정산 집계를 배치로 관리하고, 관리자 API를 통해 결과를 조회할 수 있습니다.

### 핵심 목표
- 동일 좌석 동시 요청 시 중복 예매를 방지한다.
- 예매 → 결제 대기 → 결제 성공/취소/만료 흐름의 상태 전이를 명확히 관리한다.
- Redis 캐시, QueryDSL, 인덱스를 통해 조회 성능 개선 구조를 만든다.
- 배치 기반 만료 처리와 정산 집계를 통해 운영 관점 기능까지 확장한다.

## 2. 기술 스택

### Backend
- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- QueryDSL
- Spring Cache
- Spring Scheduler / Batch 스타일 배치 처리

### Data / Infra
- MySQL
- Redis
- Docker

### Test / Docs
- JMeter
- Swagger / OpenAPI

## 3. 프로젝트 범위

### 사용자 기능
- 회원가입
- 로그인
- 내 정보 조회
- 행사 목록 조회
- 행사 상세 조회
- 행사별 회차 조회
- 회차별 좌석 조회
- 예매 요청
- 결제 Mock 성공 처리
- 예매 취소
- 내 예매 목록 조회
- 내 예매 상세 조회

### 관리자 기능
- 행사 등록
- 회차 등록
- 좌석 일괄 등록
- 일별 정산 수동 실행
- 일별 정산 단건/기간 조회

### 배치 기능
- 미결제 예매 만료 처리
- 일별 정산 집계

## 4. 핵심 도메인

- **User**: 사용자 계정 및 권한 관리
- **Event**: 행사 기본 정보
- **EventSchedule**: 행사 회차 정보
- **Seat**: 회차별 좌석 정보 및 좌석 상태 관리
- **Reservation**: 예매 상태 관리
- **Payment**: 결제 상태 관리
- **SettlementDaily**: 일별 정산 결과 저장

## 5. 주요 상태값

### ReservationStatus
- `PENDING_PAYMENT`
- `CONFIRMED`
- `CANCELED`
- `EXPIRED`

### PaymentStatus
- `READY`
- `SUCCESS`
- `FAILED`
- `CANCELED`

### SeatStatus
- `AVAILABLE`
- `RESERVED`
- `SOLD`
- `BLOCKED`

### EventStatus
- `READY`
- `OPEN`
- `CLOSED`
- `CANCELED`

## 6. 핵심 문제와 해결

### 6-1. 동일 좌석 동시 예매 충돌
#### 문제
티켓팅 시스템에서 가장 중요한 문제는 같은 좌석에 대한 동시 요청입니다.  
두 명 이상의 사용자가 같은 좌석을 거의 동시에 예매하면 중복 예매가 발생할 수 있습니다.

#### 해결
- `Seat` 엔티티에 `@Version`을 적용해 **JPA 낙관적 락** 기반 동시성 제어를 구현했습니다.
- 좌석 예매 시 `seat.reserve()`를 수행한 뒤, `reservationRepository.flush()`로 락 충돌을 가능한 빠르게 발생시키도록 했습니다.
- 회차의 `availableSeatCount`는 별도 update 쿼리로 원자 감소 처리했습니다.
- 충돌 시 의미 있는 예외 코드를 반환하도록 구성했습니다.

#### 결과
- JMeter 동시 요청 테스트를 통해 **동일 좌석 예매 시 1건만 성공**하는 것을 확인했습니다.
- 예매 정합성을 데이터베이스 레벨에서 검증 가능한 구조로 만들었습니다.

### 6-2. 예매/결제/취소/만료 상태 전이 관리
#### 문제
예매 도메인에서는 좌석 상태, 예매 상태, 결제 상태, 회차 잔여 좌석 수가 함께 바뀝니다.  
중간에 하나라도 어긋나면 데이터 정합성이 깨질 수 있습니다.

#### 해결
- 예매 요청, 결제 성공, 예매 취소, 만료 처리 각각을 **트랜잭션 단위**로 묶었습니다.
- `Reservation`, `Payment`, `Seat`, `EventSchedule.availableSeatCount`가 함께 변경되도록 설계했습니다.
- 만료된 결제 대기 예매는 `EXPIRED` 처리 후 좌석을 `AVAILABLE`로 복구하고, 가용 좌석 수를 증가시키도록 구현했습니다.

#### 결과
- 예매 흐름 전체에서 상태 전이가 일관되게 유지되도록 만들었습니다.
- 취소/만료/결제 성공 시 데이터 복구 및 상태 변경 기준이 명확해졌습니다.

### 6-3. 조회 성능 개선
#### 문제
행사 조회, 내 예매 목록 조회, 만료 배치 조회는 반복 호출이 많거나 조건 기반 조회가 자주 발생할 수 있습니다.

#### 해결
- 행사 목록/상세 조회에 Redis Cache를 적용했습니다.
- QueryDSL DTO Projection을 통해 내 예매 목록 조회를 엔티티 기반 조회에서 필요한 컬럼만 조회하는 구조로 변경했습니다.
- 다음 복합 인덱스를 추가했습니다.
  - `reservations(user_id, created_at DESC)`
  - `reservations(status, expires_at)`

#### 결과
- 캐시 hit/miss 로그를 통해 Redis 캐시 동작을 검증했습니다.
- EXPLAIN으로 복합 인덱스가 실제 실행 계획에서 사용되는 것을 확인했습니다.

### 6-4. 운영 관점 배치 처리
#### 문제
미결제 예매 만료 처리와 일별 정산은 실시간 API에서 직접 처리하기보다 비실시간 작업으로 분리하는 것이 더 적절합니다.

#### 해결
- 결제 대기 만료 예매를 찾아 상태를 정리하는 배치를 구현했습니다.
- 결제 성공 데이터를 날짜별로 집계해 `SettlementDaily`에 저장하는 일별 정산 기능을 구현했습니다.
- 관리자 API를 통해 정산 수동 실행 및 조회가 가능하도록 구성했습니다.

#### 결과
- 실시간 API 부담을 줄이면서 운영 기능을 백엔드 레벨에서 완성했습니다.
- 단순 예매 기능을 넘어 운영/집계 관점까지 확장했습니다.

## 7. 아키텍처 및 설계 포인트

### 설계 방향
- MSA로 과도하게 확장하지 않고 **Spring Boot 기반 모놀리식**으로 빠르게 완성
- 내부 코드는 **도메인 중심 패키지 구조**로 분리
- 백엔드 핵심 문제 해결에 집중

### 설계 포인트
- 인증: JWT 기반 인증/인가
- 캐시: Redis Cache
- 조회 최적화: QueryDSL + 인덱스
- 동시성: JPA 낙관적 락
- 운영 작업: 스케줄링 기반 만료/정산 처리

## 8. 주요 API 예시

### 인증
- `POST /api/v1/auth/signup`
- `POST /api/v1/auth/login`
- `GET /api/v1/users/me`

### 행사
- `GET /api/v1/events`
- `GET /api/v1/events/{eventId}`
- `GET /api/v1/events/{eventId}/schedules`
- `GET /api/v1/schedules/{scheduleId}/seats`

### 예매
- `POST /api/v1/reservations`
- `POST /api/v1/reservations/{reservationId}/payments/success`
- `POST /api/v1/reservations/{reservationId}/cancel`
- `GET /api/v1/reservations/me`
- `GET /api/v1/reservations/{reservationId}`

### 관리자
- `POST /api/v1/admin/events`
- `POST /api/v1/admin/events/{eventId}/schedules`
- `POST /api/v1/admin/schedules/{scheduleId}/seats`
- `POST /api/v1/admin/settlements/daily/run`
- `GET /api/v1/admin/settlements/daily`
- `GET /api/v1/admin/settlements/daily/range`

## 9. 테스트 및 검증

### 동시성 테스트
- JMeter를 이용해 동일 좌석에 대한 동시 예매 요청 시나리오를 구성
- 동일 좌석에 대해 1건만 성공하는지 검증

### 조회 성능 / 구조 검증
- Redis 캐시 hit/miss 로그 확인
- EXPLAIN으로 인덱스 사용 여부 확인
- QueryDSL 기반 DTO Projection 적용

### 배치 검증
- 미결제 만료 처리 결과 확인
- 일별 정산 수동 실행 및 조회 결과 확인

## 10. 실행 방법

### 1) Redis 실행
```bash
docker run -d --name flashseat-redis -p 6379:6379 redis
```

### 2) MySQL 준비
- `ticketing` 데이터베이스 생성
- `application.yml`에 DB 계정 정보 설정

### 3) 애플리케이션 실행
```bash
./gradlew bootRun
```

Windows:
```bash
.\gradlew.bat bootRun
```

### 4) Swagger 접속
```text
http://localhost:8083/swagger-ui.html
```

## 11. 향후 개선 포인트

- Redis 분산락 기반 예매 진입 제어
- 대기열 시스템 도입
- 실결제 연동
- 관리자 통계/모니터링 화면 확장
- 테스트 데이터 대량 적재 후 캐시 성능 재검증

## 12. 프로젝트를 통해 얻은 점

이 프로젝트를 통해 단순 CRUD가 아니라 다음과 같은 백엔드 핵심 문제를 직접 다뤘습니다.

- 트랜잭션 경계 설계
- 동시성 제어
- 상태 전이 관리
- 조회 최적화
- 배치 처리
- 성능 검증 및 결과 정리

FlashSeat는 기능을 많이 넣는 프로젝트보다,  
**예매 도메인 안에서 발생하는 핵심 백엔드 문제를 끝까지 해결하는 프로젝트**를 목표로 진행한 작업입니다.