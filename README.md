# Concurrent Backend API

## 해결 전략

1. 요청되는 모든 투자는 investment_event에 insert한다.
1. 요청되는 투자ID는 UUID를 생성하여 사용한다.
1. investment_event의 투자금액을 sum하여 총투자요청금액과 비교한다.    
    1. 만약 sum한 금액이 총투자요청금액을 초과하면, -투자금액을 insert 한다.
    1. 만약 sum한 금액이 총투자요청금액 이내라면, investment 테이블에 기록한다.
1. investment_event에 대한 transaction은 READ_UNCOMMITTED로 처리한다.
1. investment_event에 대한 update/delete는 없다.


## 구현
### 사용자 인증
사용자 인증은 `HttpHeaderAuthenticationFilter`에서 처리한다.   
`X-USER-ID`값이 있는 경우, userService를 이용하여 사용자 정보를 찾고 SecurityContext에 등록한다.

### Error 처리
투자주문 실패 시, InvestmentProblem 객체를 생성하여 오류를 throw 한다.

오류에 대한 응답은 `zalando/problem`을 이용하여 Response에 기록한다.   
투자주문 실패 시 422 Unprocessable Entity로 응답하고, 상세 오류코드는 `error_code` 필드에 기록한다.
```json
{
  "title": "Unprocessable Entity",
  "status": 422,
  "error_code": "INVALID_PRODUCT",
  "message_code": "error.investment.invalid_product"
}
```

#### USER_UNMATCHED
투자요청 parameter의 사용자ID와 X-USER-ID값이 일치하지 않음.
#### INVALID_PRODUCT
투자요청 상품을 찾을 수없음
#### NOT_STARTED
투자요청 상품의 접수가 시작되지 않음
#### FINISHED
투자요청 상품의 접수기간이 종료됨
#### SOLDOUT
투자요청 상품이 판매가 완료됨
#### EXCEED_LIMIT
투자금액이 투자가능 요청금액을 초과함

### DB Schema 관리 및 테스트데이터 관리
Liquibase를 이용하여 DB Schema 및 테스트데이터를 관리한다.   
Liquibase 관련 파일은 `src/main/resources/db.changelog`에서 관리한다.

* master.xml: liquibase 관리 entry point   
  `<include>`를 이용하여 수행되어야 하는 파일을 추가한다.
* 0001_initialize.xml: 초기 table schema 에 대한 ddl
* 0001_devdata.xml: 개발 및 테스트를 위한 dml   
  `master.xml` 파일에서 `context="dev"`로 기록   
  (`spring.liquibase.contexts` 속성을 이용하여 spring-profile에 따라 동작을 설정)

## API List
http://localhost:8080/swagger-ui/ 에 접속하여 확인할 수 있다.
### 상품목록 조회
```
GET http://localhost:8080/api/products?limit=10&offset=0
Content-type: application/json
X-USER-ID: 1

--------------------------------------------------
200 OK

{
  "count": 1,
  "products": [
    {
      "collectedCount": 0,
      "collectedInvestingAmount": 0,
      "finishedAt": "2021-03-22T14:30:08.983Z",
      "productId": 0,
      "startedAt": "2021-03-22T14:30:08.983Z",
      "status": "CLOSED",
      "title": "string",
      "totalInvestingAmount": 0
    }
  ]
}
```


### 상품 투자
```
POST http://localhost:8080/api/investments
Content-type: application/json
X-USER-ID: 1

{
  "investingAmount": 400,
  "productId": 20,
  "userId": 10
}

--------------------------------------------------
201 Created
Location: http://localhost:8080/api/investments/uuid-string-of-investment

{
  "createdAt": "2021-03-22T14:32:28.658Z",
  "id": "uuid-string-of-investment",
  "investingAmount": 400,
  "productId": 20,
  "title": "some product title",
  "totalInvestingAmount": 5000,
  "userId": 10
}
```

### 나의 투자상품 조회
```
GET http://localhost:8080/api/my/investments?limit=10&offset=0
Content-type: application/json
X-USER-ID: 1

--------------------------------------------------
200 OK

{
  "count": 1,
  "investments": [
    {
      "id": "uuid-4",
      "productId": 3,
      "userId": 1,
      "title": "third",
      "totalInvestingAmount": 30000,
      "investingAmount": 100,
      "createdAt": "2021-03-22T23:09:41.981438"
    }
 }
}
```
