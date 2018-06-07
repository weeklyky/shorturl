# 서버개발자 사전과제

## 프로젝트 소개
URL Shortening Service
URL을 입력받아 5자리의 짧은 문자열을 발급하고, 발급받은 문자열을 서버에 요청시 원래 URL로 리다이렉트해 주는 서비스

http://35.234.43.247/ (Temporary)


## 문제해결 전략
### Short Url 키 생성 알고리즘
Url을 Hash 하거나, uuid를 사용하는 것을 고려함 

문제 요구사항에서 키는 8 Charaters 이내의 문자열. base64로 인코딩했을 때 64^8  = 2^48, 즉 결과값이 최대 48bit 이내로는 표현되어야 중복되지 않는 키를 보장할 수 있다.

따라서 Url을 Hash하거나, uuid, 또는 (128bit)를 키로 사용하는 것은 적합하지 않다고 판단함

아예 새로운 색인 번호(ID)를 생성하되 번호를 base64로 인코딩하여 키로 사용

난수를 색인번호로 사용하는 것을 고려하였으나 URL-key간 매핑이 숨겨질 이유 없으므로 난수를 사용하는 효용성이 크지 않음-> 순차 증가하는 번호 사용

사용성(읽기,타이핑..) 위해 URL 로서 가용한 문자 중 특수문자를 제외하고 [A-Za-z0-9]의 문자 사용(base62)

### 동일한 Url은 동일한 Short Key로 응답

요청이 들어올 때마다 ID를 새로 생성하는 전략이므로, 비슷한 시간 내에 들어온 다른 요청에 대해 다른 ID를 발급해 줄 위험성 존재함.(동시성 이슈)

Short Key 발급시 비동기로 처리하되 동일URL에 대해서는 같은 응답(Future)을 리턴하도록 ConcurrentHashMap<String, Future>으로 관리

### 리다이렉트

DB에서 인코딩된 Short Key값 자체를 PK로 사용하고 있으므로 단순 데이터조회 후 redirect:를 사용함

단, 성능 향상을 위하여 Spring에서 제공하는 캐시 추상화를 이용, @Cacheable 어노테이션을 사용하여 캐시 활성화

### DB
실행환경 설정 간소화를 위해 H2 데이터베이스 사용. 
비즈니스 로직이 복잡하지 않아 JPA CRUDRepository 사용함

### 텍스트 템플릿 엔진
Thymeleaf 사용. 

### Unit Test
mvcMock과 Mockito 사용.
동시성 테스트를 위하여 JMock 사용 
코드 커버리지 90% 달성.


### Dependencies
Dependence         |Version
-------------------|-------
spring-boot       |1.5.10.RELEASE 
spring-boot-starter-web |
spring-boot-starter-data-jpa |
spring-boot-starter-cache|
spring-boot-starter-thymeleaf |
com.h2database:h2 |
gson | 2.8.2


## 빌드 및 실행

### 실행환경
- Java 1.8
- Gradle

### 포트 설정

/src/main/resources/appliaction.properties 에서 아래 포트정보를 변경후 실행 바랍니다.


`server.port=8081`


### 빌드방법

1. `gradle build`

2. `gradle bootRun`
