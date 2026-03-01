# Admin Starter 베이스 코드 구현 메뉴얼 (Long Version)

## 목차

- [1. 문서 소개](#1-문서-소개)
- [2. 프로젝트 개요](#2-프로젝트-개요)
- [3. 디렉터리 구조 요약 (트리 방식)](#3-디렉터리-구조-요약-트리-방식)
- [4. 기술 스택, 코드 규칙, 컨벤션](#4-기술-스택-코드-규칙-컨벤션)
- [5. 개발환경 준비 (IntelliJ 처음 시작 기준)](#5-개발환경-준비-intellij-처음-시작-기준)
- [6. 핵심 설정 파일 완전 해설](#6-핵심-설정-파일-완전-해설)
- [7. 다국어(i18n) 설정](#7-다국어i18n-설정)
- [8. 예외 처리 전략](#8-예외-처리-전략)
- [9. Validation 처리 전략](#9-validation-처리-전략)
- [10. 세션 권한 동기화(Auth Guard)](#10-세션-권한-동기화auth-guard)
- [11. SQL 엔티티/속성 정보 (핵심 테이블)](#11-sql-엔티티속성-정보-핵심-테이블)
- [12. Git 커밋 기반 구현 히스토리 (요약)](#12-git-커밋-기반-구현-히스토리-요약)
- [13. 시스템 메뉴별 구현 상세 (핵심 본문)](#13-시스템-메뉴별-구현-상세-핵심-본문)
- [14. 요청-권한-DB 실제 동작 시나리오 (일기형 요약)](#14-요청-권한-db-실제-동작-시나리오-일기형-요약)
- [15. 유지보수 체크리스트](#15-유지보수-체크리스트)
- [16. IntelliJ에서 처음 시작할 때의 실제 진행 순서 (실전 일지 확장판)](#16-intellij에서-처음-시작할-때의-실제-진행-순서-실전-일지-확장판)
- [17. 비즈니스 로직 상세 추적 (메뉴별 코드 발췌 강화)](#17-비즈니스-로직-상세-추적-메뉴별-코드-발췌-강화)
- [18. “코드 발췌 비율” 요구사항에 대한 문서 운영 원칙](#18-코드-발췌-비율-요구사항에-대한-문서-운영-원칙)
- [19. 설정파일 심화 발췌 (pom/web/dispatcher/context 핵심 plugin·bean·값)](#19-설정파일-심화-발췌-pomwebdispatchercontext-핵심-pluginbean값)
- [20. 메뉴별 `Controller/Service/DAO/Mapper` 핵심 메서드 전수 발췌](#20-메뉴별-controllerservicedaomapper-핵심-메서드-전수-발췌)

---

## 1. 문서 소개

이 문서는 관리자 스타터 베이스 코드를 **처음 IntelliJ에서 열고 실행환경을 맞추는 단계부터, 시스템 메뉴별 기능을 구현/수정/운영하는 단계까지**를 일지처럼 추적하는 장문 매뉴얼입니다.  
쉽게 말해, “처음 프로젝트를 맡은 개발자”가 이 문서 하나로 설정-코드-DB-권한 흐름을 끝까지 따라갈 수 있도록 만든 안내서입니다.

핵심 범위:
- 설정 파일 완전 해설: `pom.xml`, `web.xml`, `dispatcher-servlet.xml`, `context-*.xml`, DB/MyBatis, Tomcat/Maven
- 공통 정책: 예외처리, Validation, i18n, 세션 권한 동기화(Auth Guard)
- 시스템 메뉴 구현: Controller → Service → DAO → Mapper(SQL) → VO/DTO
- 커밋 히스토리 기반 구현 의도/변경 배경 정리

---

## 2. 프로젝트 개요

본 프로젝트는 Spring MVC + eGovFrame + MyBatis 조합으로 구성된 관리자 웹 애플리케이션입니다.  
쉽게 말해, URL 요청이 들어오면 컨트롤러가 받아서 서비스/DAO를 거쳐 SQL을 실행하고, JSP 또는 JSON으로 응답하는 전형적인 레이어드 아키텍처입니다.

요청 처리 축:
1. Filter 체인 (Encoding/Security/HTMLTag)
2. DispatcherServlet
3. Interceptor (`AdminAuthInterceptor`)
4. Controller
5. Service (트랜잭션 경계)
6. DAO / Mapper SQL
7. View(JSON/JSP)

---

## 3. 디렉터리 구조 요약 (트리 방식)

```text
src/
├── main/
│   ├── java/
│   │   └── toy/
│   │       ├── admin/
│   │       │   ├── main/
│   │       │   │   ├── dao/
│   │       │   │   │   └── AdmMainDAO.java
│   │       │   │   ├── service/
│   │       │   │   │   ├── impl/
│   │       │   │   │   │   └── AdmMainServiceImpl.java
│   │       │   │   │   └── AdmMainService.java
│   │       │   │   └── web/
│   │       │   │       └── AdmMainCtrl.java
│   │       │   ├── my/
│   │       │   │   ├── dao/
│   │       │   │   │   └── AdmMyMngrDAO.java
│   │       │   │   ├── service/
│   │       │   │   │   ├── impl/
│   │       │   │   │   │   └── AdmMyMngrServiceImpl.java
│   │       │   │   │   └── AdmMyMngrService.java
│   │       │   │   └── web/
│   │       │   │       └── AdmMyMngrCtrl.java
│   │       │   └── system/
│   │       │       ├── accesslog/
│   │       │       ├── allow/
│   │       │       ├── auth/
│   │       │       ├── code/
│   │       │       └── mngr/
│   │       └── com/
│   │           ├── interceptor/
│   │           │   ├── AdminAuthInterceptor.java
│   │           │   └── MybatisInterceptor.java
│   │           ├── util/
│   │           ├── validation/
│   │           └── vo/
│   ├── resources/
│   │   └── egovframework/
│   │       ├── spring/
│   │       ├── sqlmap/
│   │       ├── message/
│   │       └── egovProps/
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── web.xml
│       │   ├── config/egovframework/springmvc/dispatcher-servlet.xml
│       │   └── jsp/toy/admin/
│       ├── css/
│       ├── js/
│       └── images/
└── pom.xml
```

`admin/system/*`는 실제 업무 기능(관리자/권한/코드/IP/로그) 구현 파트입니다.  
`com/*`는 공통 인프라(권한 인터셉터, 유틸, 공통 VO, 검증 지원) 파트입니다.

---

## 4. 기술 스택, 코드 규칙, 컨벤션

### 4.1 기술 스택

- Java 11
- Spring MVC 5.x
- Spring Security 5.8.x
- eGovFrame 4.1.0
- MyBatis + XML Mapper
- MySQL 8.x
- JSP/JSTL
- Maven WAR 패키징

### 4.2 프로젝트 코딩 패턴

이 프로젝트는 Controller/Service/DAO/Mapper 계층 책임이 명확히 분리된 구조를 사용합니다.  
쉽게 말해 “화면요청 처리(Controller), 비즈니스 규칙(Service), DB 접근(DAO/SQL)”을 섞지 않는 방식입니다.

- 권한 체크 공통화: `ToyAdminAuthUtils`
- 결과코드 공통화: `CmConstants.RESULT_*`
- 검증 공통화: `@Validated(ValidationGroups.*)` + `BindingResultUtil`
- 세션 키 공통화: `CmConstants.SESSION_ADMIN_KEY`
- 강제 로그아웃 사유 코드 공통화: `FORBIDDEN`, `AUTH_CHANGED`, `DISABLED` 등

---

## 5. 개발환경 준비 (IntelliJ 처음 시작 기준)

### 5.1 JDK / Maven / Tomcat

프로젝트는 Java 11 + Maven + WAR 배포(Tomcat)를 기준으로 작성되어 있습니다.  
쉽게 말해 로컬에서 바로 띄우려면 IDE JDK/빌드 JDK/톰캣 런타임 버전을 먼저 맞춰야 합니다.

1. JDK 11 설치 및 IntelliJ Project SDK 설정
2. Maven Import (`pom.xml`)
3. Tomcat(9+) Run Configuration 생성
4. VM option 권장값: `-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Seoul`

### 5.2 DB 연결 준비

DB 접속 정보는 properties + datasource context 조합으로 구성됩니다.  
쉽게 말해 `globals.properties` 값이 `context-datasource.xml` 빈으로 주입되는 구조입니다.

```properties
# src/main/resources/egovframework/egovProps/globals.properties
jdbc.driverClassName=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/toydb?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
jdbc.username=root
jdbc.password=...
jdbc.dbType=MYSQL
```

---

## 6. 핵심 설정 파일 완전 해설

> 요청사항 반영: 아래 설정은 누락 없이, 역할과 영향까지 기록합니다.

### 6.1 `pom.xml`

이 파일은 프로젝트 의존성/빌드 규칙/패키징을 선언하는 최상위 빌드 정의서입니다.  
쉽게 말해 “이 프로젝트가 어떤 라이브러리로 어떤 결과물(WAR)을 만드는지”를 Maven에게 알려주는 파일입니다.

```text
src/
└── pom.xml
```

핵심 설정 요약:
- `<packaging>war</packaging>`
- Java 11 source/target
- Spring MVC / Security / eGovFrame / MyBatis / MySQL 의존성
- `javax.servlet-api`는 톰캣 제공이라 `provided`

핵심 스니펫:

```xml
<packaging>war</packaging>

<properties>
  <maven.compiler.source>11</maven.compiler.source>
  <maven.compiler.target>11</maven.compiler.target>
  <security.version>5.8.2</security.version>
</properties>

<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-webmvc</artifactId>
  <version>5.3.32</version>
</dependency>
```

### 6.2 `web.xml`

이 파일은 웹 애플리케이션 부트스트랩(필터/리스너/서블릿 매핑)을 담당합니다.  
쉽게 말해 “요청이 들어왔을 때 제일 먼저 작동하는 관문 설정”입니다.

```text
src/
└── main/
    └── webapp/
        └── WEB-INF/
            └── web.xml
```

핵심 포인트:
1. UTF-8 인코딩 필터
2. Spring Security FilterChain
3. HTMLTagFilter
4. Root Context 로더 (`context-*.xml`)
5. DispatcherServlet(`action`) + 매핑(`*.do`, `*.ac`, `*.doax`, `*.acax`)
6. 세션 타임아웃/쿠키 정책
7. WAS 에러 페이지 (`403/404/500`, Throwable/Exception)

핵심 스니펫:

```xml
<context-param>
  <param-name>contextConfigLocation</param-name>
  <param-value>classpath*:egovframework/spring/context-*.xml</param-value>
</context-param>

<servlet>
  <servlet-name>action</servlet-name>
  <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  <init-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/config/egovframework/springmvc/dispatcher-servlet.xml</param-value>
  </init-param>
</servlet>
```

### 6.3 `dispatcher-servlet.xml`

이 파일은 웹 계층 전용 Bean(컨트롤러, 뷰, 인터셉터, 예외해결기)을 정의합니다.  
쉽게 말해 “컨트롤러가 어떻게 매핑되고, 에러가 나면 어떤 페이지/JSON으로 보낼지”를 정하는 MVC 운영 파일입니다.

```text
src/
└── main/
    └── webapp/
        └── WEB-INF/
            └── config/
                └── egovframework/
                    └── springmvc/
                        └── dispatcher-servlet.xml
```

핵심 포인트:
- `@Controller`만 스캔(서비스/리포지토리는 Root Context)
- JSON Converter 커스텀 등록
- Locale interceptor(`lang`) + SessionLocaleResolver(default `ko`)
- ExceptionResolver 체인 + `SimpleMappingExceptionResolver`
- JSP/JSON ViewResolver 설정
- `AdminAuthInterceptor`로 `/toy/admin/**` 보호

핵심 스니펫:

```xml
<context:component-scan base-package="toy" use-default-filters="false">
  <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
</context:component-scan>

<mvc:interceptors>
  <mvc:interceptor>
    <mvc:mapping path="/toy/admin/**"/>
    <mvc:exclude-mapping path="/toy/admin/login.do"/>
    <mvc:exclude-mapping path="/toy/admin/logout.ac"/>
    <mvc:exclude-mapping path="/toy/admin/loginAction.ac"/>
    <bean class="toy.com.interceptor.AdminAuthInterceptor" />
  </mvc:interceptor>
</mvc:interceptors>
```

### 6.4 Root Context (`context-*.xml`)

Root Context는 웹 외 공통 빈(Service/DAO/DataSource/Tx/Security/MessageSource)을 담당합니다.  
쉽게 말해 “웹화면과 무관하게 애플리케이션 핵심 기능을 띄우는 본체 설정 묶음”입니다.

```text
src/
└── main/
    └── resources/
        └── egovframework/
            └── spring/
                ├── context-properties.xml
                ├── context-common.xml
                ├── context-datasource.xml
                ├── context-mapper.xml
                ├── context-transaction.xml
                ├── context-security.xml
                ├── context-validator.xml
                └── context-aspect.xml
```

#### 6.4.1 `context-properties.xml`

모든 프로퍼티 파일 로딩 기준점을 단일화합니다.  
쉽게 말해 `${...}` 치환값을 어디서 읽어올지 한 군데에서 관리합니다.

```xml
<context:property-placeholder
  location="classpath:/egovframework/egovProps/globals.properties,classpath:/prop.properties"
  ignore-resource-not-found="true"
  ignore-unresolvable="false"
  system-properties-mode="OVERRIDE" />
```

#### 6.4.2 `context-common.xml`

Service/DAO 컴포넌트 스캔과 `messageSource`를 정의합니다.  
쉽게 말해 다국어/검증메시지의 원천과 비웹 빈 스캔의 중심 파일입니다.

```xml
<context:component-scan base-package="toy">
  <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller" />
</context:component-scan>

<bean id="messageSource" class="org.springframework.context.support.ReloadableResourceBundleMessageSource">
  <property name="basenames">
    <list>
      <value>classpath:/egovframework/message/message-common</value>
    </list>
  </property>
</bean>
```

#### 6.4.3 `context-datasource.xml`

DB 커넥션 풀(DataSource)을 구성합니다.  
쉽게 말해 SQL 실행 전 단계에서 “DB 접속 통로”를 만드는 파일입니다.

```xml
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
  <property name="driverClassName" value="${jdbc.driverClassName}"/>
  <property name="url" value="${jdbc.url}"/>
  <property name="username" value="${jdbc.username}"/>
  <property name="password" value="${jdbc.password}"/>
</bean>
```

#### 6.4.4 `context-mapper.xml`

MyBatis SqlSessionFactory와 Mapper 스캔 경로를 정의합니다.  
쉽게 말해 DAO에서 부르는 SQL XML을 실제 실행 가능한 형태로 연결하는 파일입니다.

```xml
<bean id="sqlSession" class="org.mybatis.spring.SqlSessionFactoryBean">
  <property name="dataSource" ref="dataSource" />
  <property name="configLocation" value="classpath:/egovframework/sqlmap/sql-mapper-config.xml" />
  <property name="mapperLocations" value="classpath:/egovframework/sqlmap/mappers/${jdbc.dbType}/**/*.xml" />
</bean>
```

#### 6.4.5 `context-transaction.xml`

서비스 구현체 메서드에 AOP 트랜잭션 경계를 적용합니다.  
쉽게 말해 `service/impl`에서 데이터 변경 중 오류가 나면 롤백하도록 공통 정책을 부여합니다.

```xml
<aop:pointcut id="requiredTx" expression="execution(* toy..service..impl..*Impl.*(..))"/>
<tx:method name="select*" read-only="true"/>
<tx:method name="*" rollback-for="Exception"/>
```

#### 6.4.6 `context-security.xml`

보안 필터 체인과 CSRF 정책을 정의합니다.  
쉽게 말해 요청 보안 기본 규칙(정적 리소스 제외, csrf on/off 등)을 선언합니다.

#### 6.4.7 `context-validator.xml`

Bean Validation(`validator`) 및 서비스 메서드 검증 포스트프로세서를 구성합니다.  
쉽게 말해 `@Validated + BindingResult`가 작동하는 기반입니다.

### 6.5 DB/Mapper 설정 세트

DB 설정은 properties(Data) + datasource(접속) + mapper(SQL경로) 3점 구성이 핵심입니다.  
쉽게 말해 이 셋 중 하나라도 틀리면 로그인/목록조회/저장 기능이 모두 실패합니다.

```text
src/
└── main/
    └── resources/
        └── egovframework/
            ├── egovProps/
            │   └── globals.properties
            ├── spring/
            │   ├── context-datasource.xml
            │   └── context-mapper.xml
            └── sqlmap/
                ├── sql-mapper-config.xml
                └── mappers/
                    └── MYSQL/
```

### 6.6 Tomcat/Maven 설정 문서화 포인트

`server.xml`, `context.xml`, `settings.xml`은 저장소 밖에서 관리되는 경우가 많습니다.  
쉽게 말해 프로젝트 코드와 별개로 “팀 운영환경 표준값”을 문서에 명시해두지 않으면 재현성이 떨어집니다.

권장 문서화 항목:
- Tomcat: 포트, context path, JVM 옵션, session/cookie 정책
- Maven: localRepository, proxy/nexus, profile 활성화 기준

---

## 7. 다국어(i18n) 설정

이 프로젝트는 메시지 번들 + 세션 locale 방식으로 다국어를 처리합니다.  
쉽게 말해 한국어/영어 문구를 코드에 하드코딩하지 않고 키(`admin.login.fail.*`)로 분리합니다.

```text
src/
└── main/
    └── resources/
        └── egovframework/
            └── message/
                ├── message-common.properties
                ├── message-common_ko.properties
                └── message-common_en.properties
```

핵심 동작:
- `context-common.xml`: `messageSource`
- `dispatcher-servlet.xml`: `LocaleChangeInterceptor(param=lang)`, `SessionLocaleResolver(default=ko)`
- Controller: `MessageSource#getMessage(...)`로 locale별 문자열 반환

---

## 8. 예외 처리 전략

예외 처리는 WAS 레벨 + Spring MVC 레벨 이중 구조입니다.  
쉽게 말해 “컨테이너 오류”와 “애플리케이션 오류”를 분리해서 대응합니다.

1) WAS 레벨 (`web.xml`)
- `error-page` for 403/404/500
- Throwable/Exception fallback

2) Spring MVC 레벨 (`dispatcher-servlet.xml`)
- `ExceptionHandlerExceptionResolver`
- `ResponseStatusExceptionResolver`
- `DefaultHandlerExceptionResolver`
- `SimpleMappingExceptionResolver`

매핑 예:
- `DataAccessException` → DB 오류 화면
- `TransactionException` → 트랜잭션 오류 화면
- `BindException`/`TypeMismatchException` → 입력 오류 화면

---

## 9. Validation 처리 전략

검증은 그룹 기반 Bean Validation으로 구성되어 있습니다.  
쉽게 말해 등록(Create), 수정(Update), 키검증(Key)을 분리해 상황별로 다른 검증 규칙을 적용합니다.

```text
src/
└── main/
    └── java/
        └── toy/
            └── com/
                ├── validation/group/ValidationGroups.java
                └── util/BindingResultUtil.java
```

대표 패턴:

```java
public ModelAndView ajaxInsertAuthRole(
    @Validated(ValidationGroups.Create.class) @ModelAttribute("AuthVO") AuthVO vo,
    BindingResult bindingResult,
    HttpServletRequest request) {
    if (bindingResult.hasErrors()) {
        resultMap.put("result", "N");
        resultMap.put("errorMessage", BindingResultUtil.firstErrorMessage(bindingResult));
        return new ModelAndView("jsonView", resultMap);
    }
}
```

---

## 10. 세션 권한 동기화(Auth Guard)

`AdminAuthInterceptor`는 로그인 여부뿐 아니라 DB 권한 변경 여부까지 감시합니다.  
쉽게 말해 관리자의 권한이 DB에서 바뀌면 세션을 강제로 종료해 권한 불일치 상태를 방지합니다.

```text
src/
├── main/
│   ├── java/
│   │   └── toy/
│   │       └── com/
│   │           ├── interceptor/AdminAuthInterceptor.java
│   │           └── util/CmConstants.java
│   └── resources/
│       └── egovframework/sqlmap/mappers/MYSQL/admin/system/auth/AdminAuthMapper_SQL.xml
```

핵심 정책:
- `*.ac`, `*.doax`: 매 요청 Auth Guard 체크
- `*.do`: TTL(10초) 캐시 기반 체크
- 불일치 시 `logout.ac?reason=AUTH_CHANGED`

---

## 11. SQL 엔티티/속성 정보 (핵심 테이블)

아래는 실제 Mapper SQL에서 사용되는 관리자 도메인 중심 엔티티입니다.  
쉽게 말해 시스템 메뉴 기능이 어떤 테이블을 기준으로 동작하는지 파악하는 최소 데이터 모델 지도입니다.

### 11.1 `TMNGR` (관리자 계정)
- 식별: `MNGR_UID`
- 인증: `PWD_ENCPT`, `LGN_FAILR_NUMTM`, `LAST_LGN_DT`
- 인적정보: `MNGR_NM`, `EML_ADRES`, `TELNO`
- 상태: `USE_YN`

### 11.2 `TAUTH` / `TAUTH_MNGR` (권한 마스터/매핑)
- `TAUTH.AUTH_UUID`, `TAUTH.USE_YN`
- `TAUTH_MNGR.MNGR_UID`, `TAUTH_MNGR.AUTH_UUID`
- 관리자별 권한 집합 계산, Auth Guard digest 기반

### 11.3 `TADM_ALLOW_IP` (허용 IP 정책)
- 관리자별 허용 IP/CIDR/기간
- `USE_YN`, `START_DT`, `END_DT`
- 로그인 시 allowlist 검증에 사용

### 11.4 `TADM_ACCESS_LOG` (관리자 액션 로그)
- `MNGR_UID`, `ACCESS_IP`, `REQ_URI`, `ACTION_DESC`, `REG_DT`
- 메뉴별 조회(기간/아이디/IP/URI 필터)

---

## 12. Git 커밋 기반 구현 히스토리 (요약)

구현 과정은 아래 흐름으로 발전했습니다.  
쉽게 말해 초기 MVC 부팅 → 보안/예외 처리 → 로그인/권한 → 메뉴 기능 확장 → 안정화 순서입니다.

- `689c4be`: Spring MVC 기본 설정
- `92ff001`: Admin 레이아웃 + Security base
- `a1e5183`: 계층형 예외 처리/WAS 에러 페이지
- `76d0ffd`: 관리자 로그인/세션
- `6a9e100`: 로그인 응답 표준화 + i18n messageCode
- `cfb62b8`: 코드관리 E2E + Validation/BindingResult
- `7e6fbd2`, `49d077c`: context 구조/DB-MyBatis 안정화
- `e541f40`: 권한관리 기능 고도화
- `5b3cda1`: 관리자 CRUD + SMS 임시비번
- `710e15f`: 세션 권한 재검증(Auth Guard)
- `7dea51a`: Allow IP CRUD
- `428d391`: 관리자/코드 화면 i18n 보강
- `d7b3702`, `186380a`: 메인/라우팅 정리 및 미사용 정리

---

## 13. 시스템 메뉴별 구현 상세 (핵심 본문)

> 이 장은 문서의 핵심이며 가장 길어지는 파트입니다.

### 13.1 관리자 메인/로그인

관리자 로그인은 계정상태, 비밀번호, 권한보유 여부, 허용IP를 순차 검증한 뒤 세션에 권한목록을 저장하는 구조입니다.  
쉽게 말해 “아이디/비번만 맞는다고 통과되는 게 아니라, 권한과 IP까지 통과해야 로그인 성공”입니다.

```text
src/
├── main/
│   ├── java/
│   │   └── toy/
│   │       └── admin/
│   │           └── main/
│   │               ├── web/AdmMainCtrl.java
│   │               ├── service/AdmMainService.java
│   │               ├── service/impl/AdmMainServiceImpl.java
│   │               └── dao/AdmMainDAO.java
│   └── resources/
│       └── egovframework/sqlmap/mappers/MYSQL/admin/main/AdmMainMapper_SQL.xml
```

핵심 코드 스니펫:

```java
AdminLoginResult loginResult = admMainService.adminLogin(mngrVO, accessIp);
resultMap.put("result", loginResult.isSuccess() ? "Y" : "N");
resultMap.put("messageCode", messageCode);
request.getSession().setAttribute("sessionAdminVO", sessionAdminVO);
```

### 13.2 관리자관리 (System > Manager)

관리자관리 메뉴는 목록/상세/등록/수정/비활성/비밀번호 재설정까지 포함한 전체 CRUD 흐름입니다.  
쉽게 말해 관리자 계정을 생성하고 운영상태를 관리하는 백오피스 핵심 메뉴입니다.

```text
src/
├── main/
│   ├── java/
│   │   └── toy/
│   │       └── admin/system/mngr/
│   │           ├── web/AdminManagerCtrl.java
│   │           ├── service/AdminManagerService.java
│   │           ├── service/impl/AdminManagerServiceImpl.java
│   │           └── dao/AdminManagerDAO.java
│   ├── resources/
│   │   └── egovframework/sqlmap/mappers/MYSQL/admin/system/mngr/AdminManagerMapper_SQL.xml
│   └── webapp/
│       └── WEB-INF/jsp/toy/admin/system/mngr/
```

핵심 SQL 포인트:
- 목록 정렬: 사용중 우선, 권한적용 우선, 최신등록 우선
- `ACTIVE_AUTH_CNT`, `AUTH_APPLIED_YN` 동시 산출
- 소프트 삭제(`USE_YN='N'`)

### 13.3 권한관리 (System > Auth)

권한관리 메뉴는 권한 마스터 CRUD + 관리자 할당/해제를 담당하고, 변경 영향은 세션 권한 동기화까지 확장됩니다.  
쉽게 말해 권한 테이블만 고치는 메뉴가 아니라, 이미 로그인한 세션의 유효성까지 좌우하는 메뉴입니다.

```text
src/
├── main/
│   ├── java/toy/admin/system/auth/
│   │   ├── web/AdminAuthCtrl.java
│   │   ├── service/AdminAuthService.java
│   │   ├── service/impl/AdminAuthServiceImpl.java
│   │   └── dao/AdminAuthDAO.java
│   ├── resources/egovframework/sqlmap/mappers/MYSQL/admin/system/auth/AdminAuthMapper_SQL.xml
│   └── webapp/WEB-INF/jsp/toy/admin/system/auth/
```

핵심 구현 포인트:
- `ADMINISTRATOR` 보호 정책(비활성 제한)
- Validation 그룹 분리
- 중복/무효/미존재 결과를 코드로 분리 반환
- Auth Guard digest 조회 SQL 제공

### 13.4 코드관리 (System > Code)

코드관리 메뉴는 공통코드 그룹/상세코드의 등록/수정/조회/삭제와 캐시 반영을 다룹니다.  
쉽게 말해 다른 메뉴가 참조하는 기준값(상태코드, 유형코드 등)을 관리하는 기반 메뉴입니다.

```text
src/
├── main/
│   ├── java/toy/admin/system/code/
│   │   ├── web/AdminCodeCtrl.java
│   │   ├── service/AdminCodeService.java
│   │   ├── service/impl/AdminCodeServiceImpl.java
│   │   └── dao/AdminCodeDAO.java
│   ├── resources/egovframework/sqlmap/mappers/MYSQL/admin/system/code/AdminCodeMapper_SQL.xml
│   └── webapp/WEB-INF/jsp/toy/admin/system/code/
```

핵심 구현 포인트:
- 입력 검증 실패 시 `BindingResultUtil.firstErrorMessage`
- 중복/무결성 오류를 서비스 결과코드로 표준화
- 공통코드 캐시 갱신 시점 관리

### 13.5 허용 IP (System > Allow IP)

허용IP 메뉴는 로그인 접근제어의 데이터 소스입니다.  
쉽게 말해 로그인 시 허용된 IP인지 판단하는 규칙을 관리하는 보안 메뉴입니다.

```text
src/
├── main/
│   ├── java/toy/admin/system/allow/
│   │   ├── web/AdminAllowIpCtrl.java
│   │   ├── service/AdminAllowIpService.java
│   │   ├── service/impl/AdminAllowIpServiceImpl.java
│   │   └── dao/AdminAllowIpDAO.java
│   ├── resources/egovframework/sqlmap/mappers/MYSQL/admin/system/allow/AdminAllowIpMapper_SQL.xml
│   └── webapp/WEB-INF/jsp/toy/admin/system/allow/
```

핵심 구현 포인트:
- 관리자별 allow row 존재 시에만 강제
- 기간 유효성(`START_DT`, `END_DT`) 반영
- 중복/CIDR/재활성 제약 처리

### 13.6 접속 로그 (System > Access Log)

접속로그 메뉴는 관리자 행위 추적(audit) 기능입니다.  
쉽게 말해 누가 어떤 URI를 어떤 IP에서 실행했는지 사후 추적 가능한 형태로 저장/조회합니다.

```text
src/
├── main/
│   ├── java/toy/admin/system/accesslog/
│   │   ├── web/AdminAccessLogCtrl.java
│   │   ├── service/AdminAccessLogService.java
│   │   ├── service/impl/AdminAccessLogServiceImpl.java
│   │   └── dao/AdminAccessLogDAO.java
│   ├── resources/egovframework/sqlmap/mappers/MYSQL/admin/system/accesslog/AdminAccessLogMapper_SQL.xml
│   └── webapp/WEB-INF/jsp/toy/admin/system/accesslog/
```

핵심 구현 포인트:
- 검색조건(기간/관리자ID/IP/URI/행위설명)
- 페이징 조회
- 주요 액션 수행 시 로그 적재

---

## 14. 요청-권한-DB 실제 동작 시나리오 (일기형 요약)

1. 프로젝트 import 후 톰캣 실행 시 `web.xml`이 Root/Servlet context를 각각 로딩
2. 로그인 화면 진입 후 `loginAction.ac` 호출
3. 서비스에서 계정/잠금/비밀번호/권한보유/허용IP 검증
4. 로그인 성공 시 `sessionAdminVO` 저장
5. 이후 `/toy/admin/**` 요청은 인터셉터가 권한과 세션 동기화 재검증
6. 메뉴 액션 실행 시 Access Log 누적
7. 권한/허용IP 변경 시 기존 세션이 불일치하면 강제 로그아웃 유도

---

## 15. 유지보수 체크리스트

- [ ] 설정파일 변경 시(6장) 영향 범위를 반드시 문서에 반영
- [ ] 신규 메뉴 추가 시(13장) 트리/흐름/검증/권한/예외/SQL 함께 기록
- [ ] 메시지 키 추가 시 ko/en 동시 업데이트
- [ ] 커밋 타임라인(12장) 갱신
- [ ] 강제로그아웃 reason code 추가 시 화면 메시지/헬퍼 동시 반영


---

## 16. IntelliJ에서 처음 시작할 때의 실제 진행 순서 (실전 일지 확장판)

이 장은 “실제로 처음 프로젝트를 받아 개발자가 어떤 순서로 작업해야 하는지”를 운영 가능한 수준으로 재현합니다.  
쉽게 말해 IDE를 켠 뒤 어디부터 보고 무엇을 확인하고 어떤 파일부터 수정해야 하는지, 실제 작업 동선대로 적은 가이드입니다.

### 16.1 프로젝트 열기 직후

1) `pom.xml` 열어서 Java/의존성/패키징 확인  
2) `web.xml` 열어서 URL 패턴(`*.do/*.ac/*.doax/*.acax`)과 DispatcherServlet 이름 확인  
3) `dispatcher-servlet.xml` 열어서 Controller 스캔/인터셉터/뷰리졸버 확인  
4) `context-*.xml` 열어서 Root Context의 DataSource/MyBatis/Tx/Validation 배선 확인  
5) `globals.properties`에서 DB 접속값/`jdbc.dbType` 확인

### 16.2 서버 실행 전 체크

- DB 연결 가능 여부 확인
- SQL Mapper 경로가 `jdbc.dbType=MYSQL`와 일치하는지 확인
- 톰캣 실행 옵션 UTF-8/타임존 확인

### 16.3 첫 실행 후 브라우저 확인

- `/toy/admin/login.do` 진입
- 로그인 실패 메시지가 i18n 키 기준으로 내려오는지 확인
- 로그인 성공 후 `/toy/admin/main.do` 이동 확인
- 시스템 메뉴 접근 시 인터셉터 권한검사 동작 확인

---

## 17. 비즈니스 로직 상세 추적 (메뉴별 코드 발췌 강화)

> 요청하신 “비즈니스 로직 중심, 코드 발췌 + 설명”을 위해 실제 핵심 메서드/SQL을 메뉴별로 확장 정리합니다.

### 17.1 로그인/세션 수립 로직

로그인은 단순 계정 조회가 아니라 실패횟수 잠금, 암호 비교, 권한 보유, 허용IP 정책을 모두 통과해야 성공합니다.  
쉽게 말해 운영 보안 관점의 필수 조건을 순차적으로 통과하는 파이프라인입니다.

```text
src/
├── main/
│   ├── java/
│   │   └── toy/
│   │       └── admin/main/
│   │           ├── web/AdmMainCtrl.java
│   │           ├── service/impl/AdmMainServiceImpl.java
│   │           └── dao/AdmMainDAO.java
│   └── resources/
│       └── egovframework/sqlmap/mappers/MYSQL/admin/main/AdmMainMapper_SQL.xml
```

핵심 발췌(Controller):

```java
AdminLoginResult loginResult = admMainService.adminLogin(mngrVO, accessIp);
String messageCode = loginResult.getMessageCode();
String message = messageSource.getMessage(messageCode, loginResult.getMessageArgs(), "Login failed.", LocaleContextHolder.getLocale());
resultMap.put("result", loginResult.isSuccess() ? "Y" : "N");
resultMap.put("messageCode", messageCode);
resultMap.put("message", message);
request.getSession().setAttribute("sessionAdminVO", sessionAdminVO);
```

핵심 발췌(Service):

```java
SessionAdminVO sessionUserVO = admMainDAO.selectAdminUserLogin(mngrVO);
if (sessionUserVO == null) {
    admMainDAO.updateLoginFailCo(mngrVO.getMngrUid());
    result.setMessageCode("admin.login.fail.mismatchOrNoAuth");
    return result;
}

if (failCnt >= 5) {
    result.setMessageCode("admin.login.fail.locked");
    return result;
}

String encryptPw = CmUtil.encryptPassword(mngrVO.getPwdEncpt(), EgovPropertiesUtils.getProperty("DB.ENCRYPTION.KEY"));
if (!encryptPw.equals(sessionUserVO.getPwdEncpt())) {
    admMainDAO.updateLoginFailCo(mngrVO.getMngrUid());
    result.setMessageCode("admin.login.fail.mismatch");
    return result;
}

List<String> authListAll = normalizeAuthList(admMainDAO.selectAdminUserAuthList(authQueryVO));
if (authListAll == null || authListAll.isEmpty()) {
    result.setMessageCode("admin.login.fail.noAuth");
    return result;
}

boolean allowIpOk = adminAllowIpService.isAllowedLoginIp(sessionUserVO.getMngrUid(), accessIp);
if (!allowIpOk) {
    result.setMessageCode("admin.login.fail.allowIpDenied");
    return result;
}
```

핵심 발췌(SQL):

```xml
<select id="selectAdminUserLogin" resultType="SessionAdminVO">
  SELECT T_M.MNGR_UID, T_M.PWD_ENCPT, T_M.MNGR_NM, T_M.EML_ADRES, T_M.LAST_LGN_DT, T_M.LGN_FAILR_NUMTM
  FROM TMNGR T_M
  WHERE T_M.USE_YN = 'Y'
    AND T_M.MNGR_UID = #{mngrUid}
</select>

<update id="updateLastLogin">
  UPDATE TMNGR
  SET LAST_LGN_DT = NOW(), LGN_FAILR_NUMTM = 0
  WHERE MNGR_UID = #{mngrUid}
</update>
```

### 17.2 인터셉터 권한/세션 동기화(Auth Guard) 로직

인터셉터는 로그인 여부만 체크하지 않고 세션 권한과 DB 권한 digest를 동기화 검증합니다.  
쉽게 말해 권한이 바뀐 계정은 로그인 상태여도 즉시 차단되도록 만드는 안전장치입니다.

```text
src/
└── main/
    └── java/toy/com/interceptor/AdminAuthInterceptor.java
```

핵심 발췌:

```java
if (admin == null) {
    response.sendRedirect(ctx + "/toy/admin/login.do?returnURL=" + returnURL);
    return false;
}

if (shouldCheckAuthGuard(request, session, isDoax, isAc)) {
    String sessionDigest = getOrInitSessionAuthDigest(session, admin);
    AdminAuthGuardVO guard = adminAuthService.selectAdminAuthGuard(mngrUid);
    boolean ok = (guard != null) && "Y".equalsIgnoreCase(dbUseYn) && sessionDigest.equals(dbDigest);
    if (!ok) {
        return forceLogout(request, response, isAjaxLike, CmConstants.LOGOUT_REASON_AUTH_CHANGED);
    }
}

if (isProtectedAdminPath(uri, ctx)) {
    if (!hasAnyAssignedRole(admin)) {
        return forceLogout(request, response, isAjaxLike, CmConstants.LOGOUT_REASON_FORBIDDEN);
    }
}
```

### 17.3 관리자관리(Manager) 로직

관리자관리는 계정 CRUD와 권한 적용 상태를 함께 보여주는 운영 메뉴입니다.  
쉽게 말해 계정을 만들고 고치는 기능 + 해당 계정에 권한이 붙었는지 한 번에 관리하는 메뉴입니다.

핵심 발췌(SQL 목록 정렬/상태):

```xml
SELECT
  ROW_NUMBER() OVER(
    ORDER BY
      CASE WHEN T.USE_YN = 'Y' THEN 0 ELSE 1 END ASC,
      CASE WHEN IFNULL(A.ACTIVE_AUTH_CNT, 0) > 0 THEN 0 ELSE 1 END ASC,
      T.REG_DT DESC
  ) AS RN,
  T.MNGR_UID,
  T.MNGR_NM,
  IFNULL(A.ACTIVE_AUTH_CNT, 0) AS ACTIVE_AUTH_CNT,
  CASE WHEN IFNULL(A.ACTIVE_AUTH_CNT, 0) > 0 THEN 'Y' ELSE 'N' END AS AUTH_APPLIED_YN
FROM TMNGR T
LEFT JOIN (...)
ORDER BY
  CASE WHEN T.USE_YN = 'Y' THEN 0 ELSE 1 END ASC,
  CASE WHEN IFNULL(A.ACTIVE_AUTH_CNT, 0) > 0 THEN 0 ELSE 1 END ASC,
  T.REG_DT DESC
LIMIT #{firstIndex}, #{recordCountPerPage}
```

핵심 발췌(Validation 응답 패턴):

```java
if (bindingResult.hasErrors()) {
    resultMap.put("result", "N");
    resultMap.put("errorMessage", BindingResultUtil.firstErrorMessage(bindingResult));
    return new ModelAndView("jsonView", resultMap);
}
```

### 17.4 권한관리(Auth) 로직

권한관리는 권한 CRUD와 관리자 할당/해제를 수행하며, 변경 시 세션 동기화에 영향이 갑니다.  
쉽게 말해 “권한 테이블 수정”이 “실사용자 세션 강제갱신”까지 이어지는 핵심 메뉴입니다.

핵심 발췌(보호 정책):

```java
if ("ADMINISTRATOR".equalsIgnoreCase(vo.getAuthUuid()) && "N".equalsIgnoreCase(vo.getUseYn())) {
    resultMap.put("result", "Forbidden");
    resultMap.put("errorMessage", "ADMINISTRATOR role cannot be disabled");
    return new ModelAndView("jsonView", resultMap);
}
```

핵심 발췌(Auth Guard SQL 지점):

```xml
<select id="selectAdminAuthGuard" parameterType="string" resultType="AdminAuthGuardVO">
  ...
</select>
```

### 17.5 코드관리(Code) 로직

코드관리는 공통코드 그룹/상세코드 CRUD와 캐시 갱신 타이밍이 핵심입니다.  
쉽게 말해 다른 메뉴가 의존하는 기준코드를 안정적으로 유지/반영하는 메뉴입니다.

핵심 발췌(권한 실패 처리 통일):

```java
String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
if (EgovStringUtil.isNotEmpty(denyView)) {
    resultMap.put("result", "FORBIDDEN");
    resultMap.put("redirectUrl", "/toy/admin/logout.ac?reason=" + CmConstants.LOGOUT_REASON_FORBIDDEN);
    return new ModelAndView("jsonView", resultMap);
}
```

### 17.6 허용 IP(Allow IP) 로직

허용IP는 로그인 허용 여부를 결정하는 보안 정책 데이터입니다.  
쉽게 말해 계정별로 “어느 IP에서만 로그인 가능한지”를 운영자가 관리합니다.

핵심 발췌(SQL):

```xml
<select id="selectEffectiveAllowIpCountByMngrUid" parameterType="string" resultType="int">
  SELECT COUNT(*)
  FROM TADM_ALLOW_IP A
  WHERE A.MNGR_UID = #{mngrUid}
    AND A.USE_YN = 'Y'
    AND (A.START_DT IS NULL OR A.START_DT <= NOW())
    AND (A.END_DT IS NULL OR A.END_DT >= NOW())
</select>

<select id="selectEffectiveAllowIpMatchCount" resultType="int">
  SELECT COUNT(*)
  FROM TADM_ALLOW_IP A
  WHERE A.MNGR_UID = #{mngrUid}
    AND A.ALLOW_IP = #{accessIp}
    AND A.USE_YN = 'Y'
    AND (A.START_DT IS NULL OR A.START_DT <= NOW())
    AND (A.END_DT IS NULL OR A.END_DT >= NOW())
</select>
```

### 17.7 접속로그(Access Log) 로직

접속로그는 관리자 액션의 감사 추적을 담당합니다.  
쉽게 말해 장애/보안 이슈 시 누가 무엇을 했는지 역추적하는 근거 데이터입니다.

핵심 발췌(SQL):

```xml
<insert id="insertAdminAccessLog" parameterType="AdminAccessLogVO">
  INSERT INTO TADM_ACCESS_LOG (
    LOG_UUID, MNGR_UID, ACCESS_IP, REQ_URI, ACTION_DESC, MEMO, REG_DT
  ) VALUES (
    #{logUuid}, #{mngrUid}, #{accessIp}, #{reqUri}, #{actionDesc}, #{memo}, NOW()
  )
</insert>
```

---

## 18. “코드 발췌 비율” 요구사항에 대한 문서 운영 원칙

소스 전체를 기계적으로 80% 이상 복붙하면 문서 길이만 늘고 유지보수성이 급격히 나빠질 수 있습니다.  
쉽게 말해 중요한 로직은 원문 그대로 발췌하고, 반복/상수/단순 getter-setter는 구조 설명으로 대체하는 방식이 실무적으로 더 안전합니다.

권장 기준(본 문서 적용):
1. 비즈니스 분기/보안/검증/트랜잭션/쿼리 핵심은 원문 스니펫 발췌
2. UI 단순 이벤트/반복 코드/보일러플레이트는 요약
3. 변경 가능성이 높은 정책 로직은 “코드 + 설명 + 영향” 3단 구성
4. 메뉴별 최소 Controller/Service/SQL 스니펫 1세트 이상 유지

> 다음 리비전에서는 원하시면 각 메뉴별로 `Controller/Service/DAO/Mapper` 파일 단위의 “핵심 메서드 전수 발췌” 섹션을 추가해, 현재보다 더 높은 발췌 밀도로 확장할 수 있습니다.

---

## 19. 설정파일 심화 발췌 (pom/web/dispatcher/context 핵심 plugin·bean·값)

이 장은 설정 파일에서 실제로 어떤 빈/플러그인/설정값을 쓰는지 빠짐없이 추적하는 섹션입니다.  
쉽게 말해 “운영에 영향 주는 키 설정”을 코드 원문 중심으로 정리한 부분입니다.

### 19.1 `pom.xml` 핵심 의존성/버전 세팅

`pom.xml`은 런타임 동작 자체를 바꾸는 라이브러리 버전 축입니다.  
쉽게 말해 여기 버전이 바뀌면 Security/JSON/DB 동작이 같이 바뀝니다.

```xml
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <spring.maven.artifact.version>5.3.20</spring.maven.artifact.version>
    <org.egovframe.rte.version>4.1.0</org.egovframe.rte.version>
    <security.version>5.8.2</security.version>
</properties>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.32</version>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>${security.version}</version>
</dependency>
```

핵심 포인트:
- Java 11 고정
- Spring MVC 5.3.32
- Security 5.8.2
- MySQL Connector 8.0.33
- `org.egovframe.rte.*` 실행환경 패키지 사용

### 19.2 `web.xml` 필터/리스너/서블릿/세션 설정값

`web.xml`은 요청 입구에서 필터 체인 순서와 Dispatcher 진입 규칙을 확정합니다.  
쉽게 말해 여기 값이 곧 요청 라우팅과 보안처리 시작점입니다.

```xml
<filter>
    <filter-name>encodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
    <init-param>
        <param-name>forceEncoding</param-name>
        <param-value>true</param-value>
    </init-param>
</filter>

<filter>
    <filter-name>springSecurityFilterChain</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>

<filter>
    <filter-name>HTMLTagFilter</filter-name>
    <filter-class>org.egovframe.rte.ptl.mvc.filter.HTMLTagFilter</filter-class>
</filter>
```

```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath*:egovframework/spring/context-*.xml</param-value>
</context-param>

<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>

<servlet>
    <servlet-name>action</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/config/egovframework/springmvc/dispatcher-servlet.xml</param-value>
    </init-param>
    <multipart-config>
        <max-file-size>20971520</max-file-size>
        <max-request-size>52428800</max-request-size>
        <file-size-threshold>0</file-size-threshold>
    </multipart-config>
</servlet>

<servlet-mapping>
    <servlet-name>action</servlet-name>
    <url-pattern>*.do</url-pattern>
    <url-pattern>*.doax</url-pattern>
    <url-pattern>*.ac</url-pattern>
    <url-pattern>*.acax</url-pattern>
</servlet-mapping>
```

```xml
<session-config>
    <session-timeout>60</session-timeout>
    <cookie-config>
        <http-only>true</http-only>
    </cookie-config>
</session-config>
```

### 19.3 `dispatcher-servlet.xml` 핵심 bean 구성

Dispatcher context는 웹 레이어 전용 객체를 선언합니다.  
쉽게 말해 Controller 바인딩/JSON 변환/뷰렌더링/예외 매핑/인터셉터가 전부 여기서 결정됩니다.

```xml
<bean id="jsonConverter" class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
    <property name="supportedMediaTypes">
        <list>
            <value>application/json</value>
            <value>application/json;charset=UTF-8</value>
        </list>
    </property>
</bean>

<bean id="bindingInitializer" class="toy.com.egov.EgovBindingInitializer">
    <property name="validator" ref="validator" />
</bean>

<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
    <property name="webBindingInitializer" ref="bindingInitializer" />
    <property name="messageConverters">
        <list><ref bean="jsonConverter" /></list>
    </property>
</bean>
```

```xml
<bean id="localeChangeInterceptor" class="org.springframework.web.servlet.i18n.LocaleChangeInterceptor">
    <property name="paramName" value="lang" />
</bean>

<bean id="localeResolver" class="org.springframework.web.servlet.i18n.SessionLocaleResolver">
    <property name="defaultLocale" value="ko" />
</bean>
```

```xml
<bean class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
    <property name="order" value="3" />
    <property name="defaultErrorView" value="com/error/egovError"/>
    <property name="exceptionMappings">
        <props>
            <prop key="org.springframework.dao.DataAccessException">com/error/dataAccessFailure</prop>
            <prop key="org.springframework.transaction.TransactionException">com/error/transactionFailure</prop>
            <prop key="org.springframework.web.bind.MissingServletRequestParameterException">com/error/missingParameterError</prop>
            <prop key="org.springframework.beans.TypeMismatchException">com/error/typeMismatchError</prop>
            <prop key="org.springframework.validation.BindException">com/error/bindError</prop>
        </props>
    </property>
</bean>
```

```xml
<bean class="org.springframework.web.servlet.view.UrlBasedViewResolver" p:order="1"
      p:viewClass="org.springframework.web.servlet.view.JstlView"
      p:prefix="/WEB-INF/jsp/toy/" p:suffix=".jsp"
      p:contentType="text/html; charset=UTF-8"/>

<bean class="org.springframework.web.servlet.view.json.MappingJackson2JsonView" id="jsonView">
    <property name="contentType" value="application/json;charset=UTF-8"></property>
</bean>
```

```xml
<mvc:interceptors>
    <mvc:interceptor>
        <mvc:mapping path="/toy/admin/**"/>
        <mvc:exclude-mapping path="/toy/admin/login.do"/>
        <mvc:exclude-mapping path="/toy/admin/logout.ac"/>
        <mvc:exclude-mapping path="/toy/admin/loginAction.ac"/>
        <bean class="toy.com.interceptor.AdminAuthInterceptor" />
    </mvc:interceptor>
</mvc:interceptors>
```

### 19.4 Root context 파일별 bean/설정값 발췌

#### 19.4.1 `context-properties.xml`

```xml
<context:property-placeholder
    location="classpath:/egovframework/egovProps/globals.properties,classpath:/prop.properties"
    ignore-resource-not-found="true"
    ignore-unresolvable="false"
    system-properties-mode="OVERRIDE" />
```

#### 19.4.2 `context-common.xml`

```xml
<context:component-scan base-package="toy">
    <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller" />
</context:component-scan>

<bean id="messageSource" class="org.springframework.context.support.ReloadableResourceBundleMessageSource">
    <property name="basenames">
        <list>
            <value>classpath:/egovframework/message/message-common</value>
        </list>
    </property>
    <property name="defaultEncoding" value="UTF-8" />
    <property name="cacheSeconds" value="60" />
    <property name="useCodeAsDefaultMessage" value="true" />
</bean>
```

#### 19.4.3 `context-datasource.xml`

```xml
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="${jdbc.driverClassName}"></property>
    <property name="url" value="${jdbc.url}"></property>
    <property name="username" value="${jdbc.username}"></property>
    <property name="password" value="${jdbc.password}"></property>
    <property name="validationQuery" value="SELECT 1"></property>
</bean>
```

#### 19.4.4 `context-mapper.xml`

```xml
<bean id="sqlSession" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource" />
    <property name="configLocation" value="classpath:/egovframework/sqlmap/sql-mapper-config.xml" />
    <property name="mapperLocations" value="classpath:/egovframework/sqlmap/mappers/${jdbc.dbType}/**/*.xml" />
    <property name="plugins">
        <list>
            <bean class="toy.com.interceptor.MybatisInterceptor" />
        </list>
    </property>
</bean>

<bean class="org.egovframe.rte.psl.dataaccess.mapper.MapperConfigurer">
    <property name="sqlSessionFactoryBeanName" value="sqlSession"/>
    <property name="basePackage" value="toy" />
</bean>
```

#### 19.4.5 `context-transaction.xml`

```xml
<bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>

<tx:advice id="txAdvice" transaction-manager="txManager">
    <tx:attributes>
        <tx:method name="select*" read-only="true"/>
        <tx:method name="get*" read-only="true"/>
        <tx:method name="list*" read-only="true"/>
        <tx:method name="count*" read-only="true"/>
        <tx:method name="*" rollback-for="Exception"/>
    </tx:attributes>
</tx:advice>

<aop:pointcut id="requiredTx" expression="execution(* toy..service..impl..*Impl.*(..))"/>
```

#### 19.4.6 `context-security.xml`

```xml
<sec:authentication-manager alias="authenticationManager" />

<sec:http pattern="/css/**" security="none" />
<sec:http pattern="/images/**" security="none" />
<sec:http pattern="/js/**" security="none" />

<sec:http use-expressions="true">
    <sec:csrf disabled="false" />
    <sec:intercept-url pattern="/**" access="permitAll" />
    <sec:form-login />
</sec:http>
```

#### 19.4.7 `context-validator.xml`

```xml
<bean id="validator" class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean">
    <property name="validationMessageSource" ref="messageSource" />
</bean>

<bean class="org.springframework.validation.beanvalidation.MethodValidationPostProcessor">
    <property name="validator" ref="validator" />
</bean>
```

---

## 20. 메뉴별 `Controller/Service/DAO/Mapper` 핵심 메서드 전수 발췌

> 요청사항 반영: 메뉴별로 파일 단위 핵심 메서드 흐름을 Controller → Service → DAO → Mapper 순서로 전수 발췌합니다.

### 20.1 Main/Login

#### Controller (`AdmMainCtrl`)

```java
@RequestMapping({"/toy/admin/loginAction.ac"})
public ModelAndView toyAdmLoginAction(@ModelAttribute("userVO") MngrVO mngrVO, HttpServletRequest request) throws Exception {
    mngrVO.setAuthUuid(null);
    String accessIp = resolveClientIp(request);
    AdminLoginResult loginResult = admMainService.adminLogin(mngrVO, accessIp);
    ...
    request.getSession().setAttribute("sessionAdminVO", sessionAdminVO);
    return new ModelAndView("jsonView", resultMap);
}
```

#### Service (`AdmMainServiceImpl`)

```java
public AdminLoginResult adminLogin(MngrVO mngrVO, String accessIp) throws Exception {
    SessionAdminVO sessionUserVO = admMainDAO.selectAdminUserLogin(mngrVO);
    ...
    if (failCnt >= 5) { ... }
    ...
    List<String> authListAll = normalizeAuthList(admMainDAO.selectAdminUserAuthList(authQueryVO));
    if (authListAll == null || authListAll.isEmpty()) { ... }
    boolean allowIpOk = adminAllowIpService.isAllowedLoginIp(sessionUserVO.getMngrUid(), accessIp);
    if (!allowIpOk) { ... }
    admMainDAO.updateLastLogin(mngrVO.getMngrUid());
    ...
}
```

#### DAO (`AdmMainDAO`)

```java
@Mapper("AdmMainDAO")
public interface AdmMainDAO {
    SessionAdminVO selectAdminUserLogin(MngrVO mngrVO);
    List<String> selectAdminUserAuthList(MngrVO mngrVO);
    void updateLastLogin(String mngrUid);
    void updateLoginFailCo(String mngrUid);
}
```

#### Mapper (`AdmMainMapper_SQL.xml`)

```xml
<select id="selectAdminUserAuthList" resultType="String">
    SELECT TAM.AUTH_UUID
    FROM TAUTH_MNGR TAM
    INNER JOIN TAUTH TA ON TA.AUTH_UUID = TAM.AUTH_UUID
    WHERE TAM.MNGR_UID = #{mngrUid}
      AND TA.USE_YN = 'Y'
</select>
```

### 20.2 System > Manager

#### Controller (`AdminManagerCtrl`)

```java
@RequestMapping(value = "/toy/admin/system/mngr/list.do")
public String viewMngrList(@ModelAttribute("searchVO") MngrVO searchVO, ModelMap model, HttpServletRequest request) throws Exception {
    String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
    if (EgovStringUtil.isNotEmpty(denyView)) return denyView;
    adminAccessLogService.insertAdminAccessLog("Admin > System > Manager > List", request);
    return "admin/system/mngr/listMngr";
}
```

```java
@RequestMapping(value = "/toy/admin/system/mngr/insert.ac", method = RequestMethod.POST)
public ModelAndView ajaxInsertMngr(@Validated(ValidationGroups.Create.class) @ModelAttribute("MngrVO") MngrVO mngrVO,
                                   BindingResult bindingResult,
                                   HttpServletRequest request) throws Exception {
    if (bindingResult.hasErrors()) {
        resultMap.put("result", "N");
        resultMap.put("errorMessage", BindingResultUtil.firstErrorMessage(bindingResult));
        return new ModelAndView("jsonView", resultMap);
    }
    int result = adminManagerService.insertMngr(mngrVO);
    ...
}
```

#### Service (`AdminManagerServiceImpl`)

```java
@Override
public int insertMngr(MngrVO mngrVO) throws Exception {
    MngrVO dupl = adminManagerDAO.selectMngrByUserId(mngrVO);
    if (dupl != null) return CmConstants.RESULT_DUPLE;
    return adminManagerDAO.insertMngr(mngrVO);
}
```

#### DAO (`AdminManagerDAO`)

```java
@Mapper("AdminManagerDAO")
public interface AdminManagerDAO {
    List<MngrVO> selectMngrList(MngrVO searchVO);
    int selectMngrListCount(MngrVO searchVO);
    MngrVO selectMngr(MngrVO mngrVO);
    int insertMngr(MngrVO mngrVO);
    int updateMngr(MngrVO mngrVO);
    int softDeleteMngr(MngrVO mngrVO);
}
```

#### Mapper (`AdminManagerMapper_SQL.xml`)

```xml
<insert id="insertMngr" parameterType="toy.com.vo.system.mngr.MngrVO">
    INSERT INTO TMNGR (
        MNGR_UID, PWD_ENCPT, MNGR_NM, EML_ADRES, TELNO, REG_DT, REG_UID, USE_YN
    ) VALUES (
        #{mngrUid}, #{pwdEncpt}, #{mngrNm}, #{emlAdres}, #{telno}, NOW(), #{regUid}, IFNULL(#{useYn}, 'Y')
    )
</insert>
```

### 20.3 System > Auth

#### Controller (`AdminAuthCtrl`)

```java
@RequestMapping(value = "/toy/admin/system/auth/role/insert.ac", method = RequestMethod.POST)
public ModelAndView ajaxInsertAuthRole(@Validated(ValidationGroups.Create.class) @ModelAttribute("AuthVO") AuthVO vo,
                                       BindingResult bindingResult,
                                       HttpServletRequest request) throws Exception {
    if (bindingResult.hasErrors()) { ... }
    int affected = adminAuthService.insertAdminAuthRole(vo);
    ...
}
```

#### Service (`AdminAuthServiceImpl`)

```java
@Override
public int updateAdminAuthRole(AuthVO vo) throws Exception {
    if (vo == null || EgovStringUtil.isEmpty(vo.getAuthUuid())) return CmConstants.RESULT_INVALID;
    return adminAuthDAO.updateAdminAuthRole(vo);
}
```

#### DAO (`AdminAuthDAO`)

```java
@Mapper("AdminAuthDAO")
public interface AdminAuthDAO {
    List<AuthVO> selectAdminAuthRoleList(AuthVO searchVO);
    int selectAdminAuthRoleListCnt(AuthVO searchVO);
    int insertAdminAuthRole(AuthVO vo);
    int updateAdminAuthRole(AuthVO vo);
    AdminAuthGuardVO selectAdminAuthGuard(String mngrUid);
}
```

#### Mapper (`AdminAuthMapper_SQL.xml`)

```xml
<select id="selectAdminAuthGuard" parameterType="string" resultType="AdminAuthGuardVO">
    SELECT
        M.MNGR_UID AS mngrUid,
        M.USE_YN AS mngrUseYn,
        SHA2(IFNULL(GROUP_CONCAT(DISTINCT UPPER(TRIM(AM.AUTH_UUID)) ORDER BY UPPER(TRIM(AM.AUTH_UUID)) SEPARATOR '|'), ''), 256) AS authDigest
    FROM TMNGR M
    LEFT JOIN TAUTH_MNGR AM ON AM.MNGR_UID = M.MNGR_UID
    LEFT JOIN TAUTH A ON A.AUTH_UUID = AM.AUTH_UUID AND A.USE_YN = 'Y'
    WHERE M.MNGR_UID = #{mngrUid}
    GROUP BY M.MNGR_UID, M.USE_YN
</select>
```

### 20.4 System > Code

#### Controller (`AdminCodeCtrl`)

```java
@RequestMapping(value = "/toy/admin/system/code/group/insert.ac", method = RequestMethod.POST)
public ModelAndView ajaxInsertCdGrp(@Validated(ValidationGroups.Create.class) @ModelAttribute("CdGrpVO") CdGrpVO vo,
                                    BindingResult bindingResult,
                                    HttpServletRequest request) throws Exception {
    String denyView = ToyAdminAuthUtils.chkAdminCrudPermission(MENU_ROLE);
    if (EgovStringUtil.isNotEmpty(denyView)) { ... }
    if (bindingResult.hasErrors()) { ... }
    int affected = adminCodeService.insertCdGrp(vo);
    ...
}
```

#### Service (`AdminCodeServiceImpl`)

```java
@Override
public int insertCdGrp(CdGrpVO vo) throws Exception {
    if (vo == null) return CmConstants.RESULT_INVALID;
    return adminCodeDAO.insertCdGrp(vo);
}
```

#### DAO (`AdminCodeDAO`)

```java
@Mapper("AdminCodeDAO")
public interface AdminCodeDAO {
    List<CdGrpVO> selectCdGrpList(CdGrpVO searchVO);
    int insertCdGrp(CdGrpVO vo);
    int updateCdGrp(CdGrpVO vo);
    int deleteCdGrp(CdGrpVO vo);
}
```

#### Mapper (`AdminCodeMapper_SQL.xml`)

```xml
<select id="selectCdGrpList" parameterType="CdGrpVO" resultType="CdGrpVO">
    SELECT CD_GRP_ID, CD_GRP_NM, USE_YN, REG_DT
    FROM TCM_CD_GRP
    ...
</select>
```

### 20.5 System > Allow IP

#### Controller (`AdminAllowIpCtrl`)

```java
@RequestMapping(value = "/toy/admin/system/allow/list.do")
public String viewAllowIpList(@ModelAttribute("searchVO") AdminAllowIpSearchVO searchVO,
                              HttpServletRequest request) throws Exception {
    String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
    if (EgovStringUtil.isNotEmpty(denyView)) return denyView;
    adminAccessLogService.insertAdminAccessLog("Admin > System > Allow IP > List", request);
    return "admin/system/allow/listAllowIp";
}
```

#### Service (`AdminAllowIpServiceImpl`)

```java
@Override
public boolean isAllowedLoginIp(String mngrUid, String accessIp) throws Exception {
    int effectiveCnt = adminAllowIpDAO.selectEffectiveAllowIpCountByMngrUid(mngrUid);
    if (effectiveCnt <= 0) return true;
    int matchCnt = adminAllowIpDAO.selectEffectiveAllowIpMatchCount(mngrUid, accessIp);
    return matchCnt > 0;
}
```

#### DAO (`AdminAllowIpDAO`)

```java
@Mapper("AdminAllowIpDAO")
public interface AdminAllowIpDAO {
    int selectEffectiveAllowIpCountByMngrUid(String mngrUid);
    int selectEffectiveAllowIpMatchCount(@Param("mngrUid") String mngrUid, @Param("accessIp") String accessIp);
    int insertAdminAllowIp(AdminAllowIpVO vo);
    int updateAdminAllowIp(AdminAllowIpVO vo);
}
```

#### Mapper (`AdminAllowIpMapper_SQL.xml`)

```xml
<select id="selectEffectiveAllowIpMatchCount" resultType="int">
    SELECT COUNT(*)
    FROM TADM_ALLOW_IP A
    WHERE A.MNGR_UID = #{mngrUid}
      AND A.ALLOW_IP = #{accessIp}
      AND A.USE_YN = 'Y'
      AND (A.START_DT IS NULL OR A.START_DT <= NOW())
      AND (A.END_DT IS NULL OR A.END_DT >= NOW())
</select>
```

### 20.6 System > Access Log

#### Controller (`AdminAccessLogCtrl`)

```java
@RequestMapping(value = "/toy/admin/system/accesslog/list.do")
public String viewAccessLogList(@ModelAttribute("searchVO") AdminAccessLogSearchVO searchVO,
                                HttpServletRequest request) throws Exception {
    String denyView = ToyAdminAuthUtils.chkAdminMenuPermission(MENU_ROLE);
    if (EgovStringUtil.isNotEmpty(denyView)) return denyView;
    adminAccessLogService.insertAdminAccessLog("Admin > System > Access Log > List", request);
    return "admin/system/accesslog/listAccessLog";
}
```

#### Service (`AdminAccessLogServiceImpl`)

```java
@Override
public int insertAdminAccessLog(String actionDesc, HttpServletRequest request) throws Exception {
    AdminAccessLogVO vo = new AdminAccessLogVO();
    vo.setActionDesc(actionDesc);
    vo.setReqUri(request.getRequestURI());
    vo.setAccessIp(resolveClientIp(request));
    return adminAccessLogDAO.insertAdminAccessLog(vo);
}
```

#### DAO (`AdminAccessLogDAO`)

```java
@Mapper("AdminAccessLogDAO")
public interface AdminAccessLogDAO {
    List<AdminAccessLogVO> selectAdminAccessLogList(AdminAccessLogSearchVO searchVO);
    int selectAdminAccessLogCount(AdminAccessLogSearchVO searchVO);
    int insertAdminAccessLog(AdminAccessLogVO vo);
}
```

#### Mapper (`AdminAccessLogMapper_SQL.xml`)

```xml
<select id="selectAdminAccessLogList" parameterType="AdminAccessLogSearchVO" resultType="AdminAccessLogVO">
    SELECT LOG_UUID AS logUuid, MNGR_UID AS mngrUid, ACCESS_IP AS accessIp,
           REQ_URI AS reqUri, ACTION_DESC AS actionDesc, MEMO AS memo, REG_DT AS regDt
    FROM TADM_ACCESS_LOG
    <include refid="whereSelectAdmAcssLog" />
    ORDER BY REG_DT DESC
    LIMIT #{firstIndex}, #{recordCountPerPage}
</select>
```
