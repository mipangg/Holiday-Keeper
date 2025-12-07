# 🏖️ Holiday Keeper

## 프로젝트 목표
외부 API 두 개로 최근 5 년(2020 ~ 2025)의 전 세계 공휴일 데이터를 저장·조회·관리하는 Mini Service 구현

---

## 🚀 빌드 & 실행 방법
### 사전 요구사항
- JDK 21
- Gradle Wrapper(프로젝트에 포함)
- 외부 API 호출을 위한 인터넷 

### 실행 방법

```bash

./gradlew clean build

./gradlew bootRun

```

서버 기본 Port: `8080`

---

## 🛜 REST API 명세
### 1. 공휴일 데이터 적재 
``` POST /holidays ```
- 최근 5 년(2020 ~ 2025)의 공휴일을 외부 API에서 수집하여 DB에 저장 
- 최초 실행시 시 5 년 × N 개 국가를 일괄 적재
#### Status
| status | response content |
| --- | --- |
| 201 | CREATED |
| 400 | BAD REQUEST |


### 2. 공휴일 삭제 
```DELETE /holidays/{year}/{countryCode}```
- 특정 연도의 특정 국가 공휴일 데이터 전체 삭제

#### Path Variables
| key | 설명 | value 타입 | 예시 |
| --- | --- | --- | --- |
| year | 조회 연도 | int | 2025 |
| countryCode | 국가 코드 | String | “KR” |

#### Status
| status | response content |
| --- | --- |
| 204 | NO CONTENT |
| 400 | BAD REQUEST |
| 404 | NOT FOUND |

### 3. 공휴일 재동기화 
```PUT /holidays/{year}/{countryCode}```
- 특정 연도의 국가 공휴일 데이터를 삭제 후 재호출하여 다시 저장

#### Path Variables
| key | 설명 | value 타입 | 예시 |
| --- | --- | --- | --- |
| year | 조회 연도 | int | 2025 |
| countryCode | 국가 코드 | String | “KR” |

#### Status
| status | response content |
| --- | --- |
| 200 | OK |
| 400 | BAD REQUEST |

### 4. 공휴일 검색 
```GET /holidays/{year}/{countryCode}```
- 연도별·국가별 필터 기반 공휴일 조회
- from ~ to 기간 필터링 기능
- 공휴일 타입 필터링 기능
- 페이징 형태로 결과 응답

#### Path Variables
| key | 설명 | value 타입 | 예시 |
| --- | --- | --- | --- |
| year | 조회 연도 | int | 2025 |
| countryCode | 국가 코드 | String | “KR” |

#### Query parameter(HolidaySearchRequest)

| 파라미터 | value 타입 | 설명 | 기본값 | Nullable | 예시 |
| --- | --- | --- | --- | --- | --- |
| page | int | 페이지 번호 | 0 | X | 0 |
| size | int | 페이지 크기 | 20 | X | 20 |
| from | LocalDate | 시작 날짜 필터 | null | O | 2025-01-01 |
| to | LocalDate | 종료 날짜 필터 | null | O | 2025-12-31 |
| holidayType | String | 공휴일 타입 | null | O | “Public” |

#### Response
| key | value 타입 | 설명 | Nullable | 예시 |
| --- | --- | --- | --- | --- |
| date | LocalDate | 공휴일 날짜 | X | 2025-01-01 |
| localName | String | 지역 명칭 | X | 새해 |
| name | String | 영어 명칭 | X | New Year's Day |
| country | String | 국가명 | X | South Korea |
| countryCode | String | 국가 코드 | X | KR |
| isGlobal | Boolean | 전역 공휴일인지 | X | true |
| counties | List<String> | 공휴일 적용 범위 | O | [ "CA-AB","CA-SK"] |
| types | List<String> | 공휴일 타입 | X | ["Public"] |

#### Example

```json
{
    "content": [
        {
            "date": "2025-01-01",
            "localName": "New Year's Day",
            "name": "New Year's Day",
            "country": "Canada",
            "countryCode": "CA",
            "isGlobal": true,
            "counties": [],
            "types": [
                "Public"
            ]
        },
        {
            "date": "2025-02-17",
            "localName": "Louis Riel Day",
            "name": "Louis Riel Day",
            "country": "Canada",
            "countryCode": "CA",
            "isGlobal": false,
            "counties": [
                "CA-MB"
            ],
            "types": [
                "Public"
            ]
        },
        {
            "date": "2025-02-17",
            "localName": "Islander Day",
            "name": "Islander Day",
            "country": "Canada",
            "countryCode": "CA",
            "isGlobal": false,
            "counties": [
                "CA-PE"
            ],
            "types": [
                "Public"
            ]
        },
        {
            "date": "2025-02-17",
            "localName": "Heritage Day",
            "name": "Heritage Day",
            "country": "Canada",
            "countryCode": "CA",
            "isGlobal": false,
            "counties": [
                "CA-NS"
            ],
            "types": [
                "Public"
            ]
        },
        {
            "date": "2025-02-17",
            "localName": "Family Day",
            "name": "Family Day",
            "country": "Canada",
            "countryCode": "CA",
            "isGlobal": false,
            "counties": [
                "CA-AB",
                "CA-BC",
                "CA-NB",
                "CA-ON",
                "CA-SK"
            ],
            "types": [
                "Public"
            ]
        },
        {
            "date": "2025-03-17",
            "localName": "Saint Patrick's Day",
            "name": "Saint Patrick's Day",
            "country": "Canada",
            "countryCode": "CA",
            "isGlobal": false,
            "counties": [
                "CA-NL"
            ],
            "types": [
                "Public"
            ]
        },
        {
            "date": "2025-04-18",
            "localName": "Good Friday",
            "name": "Good Friday",
            "country": "Canada",
            "countryCode": "CA",
            "isGlobal": true,
            "counties": [],
            "types": [
                "Public"
            ]
        },
        {
            "date": "2025-04-21",
            "localName": "Easter Monday",
            "name": "Easter Monday",
            "country": "Canada",
            "countryCode": "CA",
            "isGlobal": false,
            "counties": [
                "CA-AB",
                "CA-PE"
            ],
            "types": [
                "Public"
            ]
        },
        {
            "date": "2025-04-23",
            "localName": "Saint George's Day",
            "name": "Saint George's Day",
            "country": "Canada",
            "countryCode": "CA",
            "isGlobal": false,
            "counties": [
                "CA-NL"
            ],
            "types": [
                "Public"
            ]
        },
        {
            "date": "2025-05-19",
            "localName": "National Patriots' Day",
            "name": "National Patriots' Day",
            "country": "Canada",
            "countryCode": "CA",
            "isGlobal": false,
            "counties": [
                "CA-QC"
            ],
            "types": [
                "Public"
            ]
        },
        {
            "date": "2025-05-19",
            "localName": "Victoria Day",
            "name": "Victoria Day",
            "country": "Canada",
            "countryCode": "CA",
            "isGlobal": true,
            "counties": [],
            "types": [
                "Public"
            ]
        }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 11,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
}
```

#### Status

| status | response content |
| --- | --- |
| 200 | OK |
| 400 | BAD REQUEST |
| 404 | NOT FOUND |

### 외부 공휴일 데이터 동기화
- 프로젝트 실행 시 자동으로 동기화
- 국가 목록 호출하여 DB에 저장
- 연도별 국가 공휴일 호출하여 DB에 저장
- Country·Holiday·HolidayCounty·HolidayType 매핑
- 초기 데이터는 `HolidayBatchInitializer` 에 의해 자동 실행

--- 

## 🛠️ ERD

<img width="1120" height="522" alt="Image" src="https://github.com/user-attachments/assets/bfa7489c-1c82-48da-8519-01c32bb6403c" />

---

## 🧪 테스트 실행
- ```./gradlew clean test``` 성공 스크린샷

<img width="350" height="571" alt="Image" src="https://github.com/user-attachments/assets/ffbb3a36-9e85-4b6d-ae35-53d98a16ccf5" />

---

## 📄 Swagger UI / OpenAPI 확인 방법
- 프로젝트 실행 후 브라우저에서 접속:

#### Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

<img width="1304" height="650" alt="Image" src="https://github.com/user-attachments/assets/cd73755e-2e89-417b-9281-54c528cb1690" />

#### OpenAPI JSON
```
http://localhost:8080/v3/api-docs
```

<img width="689" height="941" alt="Image" src="https://github.com/user-attachments/assets/c8d26425-8c0c-43df-8c97-f0a5d4f5b531" />

Swagger UI와 OpenAPI JSON 문서는 springdoc-openapi에 의해 자동 구성



---

## 패키지 구조

```
holidaykeeper/
├── HolidayKeeperApplication.java
├── config/
└── domain/
    ├── common/
    ├── country/
    │   ├── dto/
    │   ├── entity/
    │   ├── repository/
    │   └── service/
    ├── county/
    │   ├── entity/
    │   ├── repository/
    │   └── service/
    └── holiday/
        ├── controller/
        ├── dto/
        ├── entity/
        ├── repository/
        ├── service/
        └── util/
```

---

## ⚙ 기술 스택
### BE
- Java 21
- Spring Boot 3.4.12
- Spring Data JPA
- Spring WebFlux
- Spring Validation

### DB
- H2

### API 문서화
- SpringDoc OpenAPI 3
- Swagger UI

### 테스트
- JUnit 5, Spring Boot Test, Mockito

### Utilities
- Lombok