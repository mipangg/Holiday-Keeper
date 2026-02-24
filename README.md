## 📖 프로젝트 소개
- 프로젝트명: Holiday Keeper
- 한 줄 소개: 외부 공휴일 API를 연동하여 국가·연도별 공휴일을 저장·조회·동기화하는 백엔드 시스템
- 개발 기간: 2026.01.29 ~ 2026.02.23
- 기획 의도: 외부 API 두 개만으로 특정 기간의 전 세계 공휴일 데이터를 저장·조회·관리하는 Mini Service 구현

## 🛠 Tech Stack

### Backend
- Java 21
- Spring Boot 3.5.10
    - Spring Web(MVC)
    - Spring WebFlux(Reactive HTTP Client)
    - Spring Data JPA
    - Spring Validation
- JPA(Hibernate)
- QueryDSL 5.0(Jakarta)
- Lombok
- SpringDoc OpenAPI(Swagger UI)

### Database
- H2

### Test
- JUnit5
- AssertJ
- Mockito
- MockWebServer(외부 API 테스트용)

### Build Tool
- Gradle

## 📂 프로젝트 구조

```
holidaykeeper/
├── HolidayKeeperApplication.java
├── domain/
│   ├── common/
│   ├── country/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── county/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── holiday/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── properties/
│   │   ├── repository/
│   │   └── service/
│   ├── holidayCounty/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── holidayType/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   └── type/
│       ├── entity/
│       ├── repository/
│       └── service/
├── external/
│   ├── config/
│   ├── dto/
│   └── service/
└── global/
    ├── config/
    └── exception/

```

## ✨ 주요 기능

### 1. 공휴일 데이터 적재
- 최근 1년간의 공휴일 데이터를 외부 API로부터 수집 
- 연도 범위는 application.yml 설정값으로 관리 
- 연도 변경 시 코드 수정 없이 확장 가능

```yaml
holiday:
  fetch-years: 1
```
✔ 설정 기반 확장 구조   
✔ 운영 환경 변경에 유연하게 대응

### 2. 공휴일 검색(필터링 + 페이징)
- 연도 및 국가 기준으로 공휴일을 조회
- 기간 필터(from ~ to)
- 공휴일 타입 필터(type)
- 페이징 응답

```
GET /holidays/{year}/{countryCode}?from=2025-01-01&to=2025-03-31&type=Public
```

✔ QueryDSL 기반 동적 필터링   
✔ Fetch Join + ID 기반 페이징 최적화

### 3. 재동기화 (Upsert)
- 특정 연도·국가 데이터를 재호출하여 동기화(Upsert)

- 동작 방식 
  - 새 데이터와 DB 모두에 존재하면 → 업데이트 
  - 새 데이터에는 존재하지만 DB에 없으면 → 신규 insert 
  - DB에만 존재하면 → 삭제 처리

✔ 외부 API와의 데이터 정합성 유지   
✔ 복합 유니크 제약으로 중복 방지

### 4. 삭제
- 특정 연도·국가의 공휴일 데이터를 전체 삭제 
- Holiday 테이블은 Soft Delete 적용 
- 마스터 테이블(Country, Type 등)은 유지

✔ Soft Delete 범위 최소화 전략 적용

## 📌 API 명세

| Method | URL                            | 설명                   |
|--------|--------------------------------|----------------------|
| POST   | /holidays                      | 공휴일 생성               |
| GET    | /holidays/{year}/{countryCode} | 특정 연도·국가의 공휴일 조회     |
| PUT    | /holidays/{year}/{countryCode} | 특정 연도·국가의 공휴일 upsert |
| DELETE | /holidays/{year}/{countryCode} | 특정 연도·국가의 공휴일 데이터 삭제 |

- Swagger: http://localhost:8080/swagger-ui.html

## 🗄 ERD

<img width="1130" height="633" alt="Image" src="https://github.com/user-attachments/assets/cfc95b7f-1019-4d2a-9f11-0378c31a73a0" />

## 🚨 Trouble Shooting

### 1. 외부 API 중복 데이터 저장 문제

#### 문제

외부 공휴일 API 재호출 또는 upsert 시 동일 데이터가 중복 저장될 가능성이 있었다.

#### 해결

DB 레벨에서 복합 유니크 제약을 설정하여 데이터 무결성을 보장했다.

```java
@Entity
@Table(
    name = "holiday",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_holiday_date_local_name_country_id_deleted",
            columnNames = {"date", "local_name", "country_id", "deleted"}
        )
    },
    indexes = {
        @Index(
            name = "idx_holiday_country_date_deleted",
            columnList = "country_id, date, deleted"
        )
    }
)
```

✔ 애플리케이션 로직이 아닌 DB 차원에서 중복 방지   
✔ 조회 성능을 고려한 복합 인덱스 추가


### 2. Fetch Join + 페이징 충돌 문제(N+1 해결)

#### 문제

Fetch Join과 페이징을 동시에 사용하면 중복 row 발생 및 정확한 페이징이 불가능했다.

#### 해결

ID 기반 2단계 조회 전략을 적용하였다.

1. ID만 먼저 조회(페이징 적용)
2. 실제 엔티티를 fetch join으로 재조회

```java
// 1단계: ID 조회
.select(holiday.id)
.offset(pageable.getOffset())
.limit(pageable.getPageSize())

// 2단계: fetch join 조회
.selectFrom(holiday)
.join(holiday.country).fetchJoin()
.where(holiday.id.in(idList))
```

✔ 정확한 페이징 보장   
✔ N+1 문제 방지

## ⚙️ 설계 고민

### 1. Soft Delete 범위 최소화 전략

#### 결정

Soft Delete는 Holiday 테이블에만 적용

#### 이유

- 연관 데이터는 Holiday가 없으면 의미 없음 
- 복구 가능성이 낮은 데이터 
- 쿼리 복잡도 최소화

### 2. QueryDSL 도입(동적 필터링 해결)

`type`, `from`, `to` 조건이 모두 nullable인 구조에서
가독성과 유지보수를 위해 QueryDSL을 도입하였다.

```java
BooleanBuilder builder = new BooleanBuilder();

if (type != null) {
    builder.and(holiday.type.eq(type));
}
if (start != null) {
    builder.and(holiday.date.goe(start));
}
if (end != null) {
    builder.and(holiday.date.loe(end));
}
```

✔ 동적 쿼리를 깔끔하게 구성   
✔ 컴파일 타임 타입 체크   
✔ 실무 친화적 설계   

### 3. 설정 기반 확장 구조

외부 API 수집 연도 범위를 `application.yml`로 분리하였다.

```yaml
holiday:
  fetch-years: 1
```

✔ 코드 수정 없이 운영 정책 변경 가능   
✔ 환경(dev/prod)별 설정 분리 가능   
✔ 테스트 환경 주입 가능   

## 🧪 테스트 전략

- 단위 테스트
- 슬라이스 테스트

### 테스트 예시
- MockWebServer를 활용한 외부 API 테스트
- Holiday 저장 성공 테스트
- Holiday 저장 시도 시 이미 중복된 Holiday 데이터가 있는 경우의 실패 테스트
- TestUtils 활용

## 🚀 실행 방법

### 1. 환경 요구 사항
- Java 21 
- Gradle 
- H2

### 2. 프로젝트 실행
```
./gradlew build
./gradlew bootRun
```
또는
```
./gradlew clean build
java -jar build/libs/holidaykeeper-0.0.1-SNAPSHOT.jar
```

## 💡 회고
### 배운 점
- DB 레벨 제약의 중요성
- ID 기반 페이징 전략
- 동적 쿼리 설계의 필요성
- Soft Delete 최소화의 중요성
- 설정 분리를 통한 확장 가능한 구조 설계

### 다음 개선 방향
- 통합 테스트 확장 
- 캐싱 전략 도입 
- 비동기 처리(WebFlux) 확장 실험 
- 운영 환경(MySQL) 적용 및 인덱스 튜닝