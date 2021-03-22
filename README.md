# Concurrent Backend API

## 해결 전략

### 핵심 해결전략
1. 요청되는 모든 투자는 investment_event에 insert한다.
1. investment_event의 투자금액을 sum하여 총투자요청금액과 비교한다.    
    1. 만약 sum한 금액이 총투자요청금액을 초과하면, -투자금액을 insert 한다.
    1. 만약 sum한 금액이 총투자요청금액 이내라면, investment 테이블에 기록한다.
1. investment_event에 대한 transaction은 READ_UNCOMMITTED로 처리한다.

### 사용자 인증처리
OncePerRequestFilter를 확장한 HttpHeaderAuthenticationFilter를 구현하여 인증을 처리.   
X-USER-ID값이 있는 경우, userService를 이용하여 사용자 정보를 찾아 SecurityContext에 등록.

## API List

