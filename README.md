# Goodit Assignment - 배포 실행 가이드

AWS EC2(Ubuntu 24.04 LTS) 환경에 Spring Boot 애플리케이션을 배포하기 위한 빌드, DB 설정, 실행, 포트, 계정 정보를 정리한 문서입니다.

## 0. 서버 접속 정보
| 항목 | 값 |
|---|---|
| 서버 퍼블릭 IP | `52.79.250.9` |
| SSH 접속 계정 | `ubuntu` |
| 웹 접속 주소 | `http://52.79.250.9` |

```bash
ssh -i <제공된 키 파일>.pem ubuntu@52.79.250.9
```

## 1. 환경 정보

| 항목 | 값 |
|---|---|
| OS | Ubuntu 24.04 LTS |
| 인스턴스 타입 | t3.small |
| Java | OpenJDK 17 |
| 빌드 도구 | Gradle (Wrapper 사용, `./gradlew`) |
| 프레임워크 | Spring Boot 3.5.x |
| DB | MySQL 8.0 |
| WAS | 내장 Tomcat (Spring Boot embedded) |
| 리버스 프록시 | nginx |

## 2. 포트 정보

| 포트 | 용도 | 외부 공개 여부 |
|---|---|---|
| 22 | SSH 접속 | 공개 (보안 그룹에서 내 IP로 제한 권장) |
| 80 | nginx (리버스 프록시, 외부 진입점) | 공개 |
| 8080 | Spring Boot 내장 Tomcat | 비공개 (localhost 내부 통신만 사용) |
| 3306 | MySQL | 비공개 (localhost 내부 통신만 사용, 보안 그룹에서 닫아둠) |

외부 사용자는 80번 포트로만 접속하며, nginx가 내부적으로 8080번으로 요청을 전달합니다. 8080, 3306 포트는 AWS 보안 그룹 인바운드 규칙에 추가하지 않았습니다.

## 3. 계정 정보

### 3-1. OS 계정

| 계정 | 용도 |
|---|---|
| `ubuntu` | SSH 접속, 소스 클론 및 빌드 작업용 |
| `appuser` | 애플리케이션 실행 전용 시스템 계정(`useradd -r -s /bin/false`), 로그인 불가, 권한 최소화 |

### 3-2. MySQL 계정

| 계정 | 권한 범위 | 비밀번호 |
|---|---|---|
| `root`@`localhost` | 전체 서버 관리용 (애플리케이션에서는 사용하지 않음) | `mysql_secure_installation` 시 설정 |
| `goodit`@`localhost` | `goodit` 데이터베이스에 대한 모든 권한 (애플리케이션 연동 계정) | `admin` |


### 3-3. 애플리케이션 샘플 로그인 계정

| username | password | email | 비고 |
|---|---|---|---|
| `tester` | password123 | tester@goodit.kr | 샘플 테스트 계정 |

## 4. DB 설정

### 4-1. MySQL 설치

```bash
sudo apt update
sudo apt install mysql-server -y
sudo systemctl enable mysql
sudo systemctl start mysql
```

### 4-2. 보안 설정

```bash
sudo mysql_secure_installation
```

### 4-3. 데이터베이스 및 계정 생성

`root` 계정으로 접속하여 1회만 실행합니다.

```bash
sudo mysql -u root -p
```

```sql
CREATE DATABASE goodit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'goodit'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON goodit.* TO 'goodit'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 4-4. 스키마

저장소에 포함된 `schema.sql`을 사용합니다.

```bash
mysql -u goodit -p goodit < schema.sql
```

#### 4-4-1. MEMBER
```sql
CREATE TABLE member (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)   NOT NULL,
    password    VARCHAR(60)   NOT NULL,
    email       VARCHAR(100)  NOT NULL,
    created_at  DATETIME(6)   NOT NULL,
    CONSTRAINT uk_member_username UNIQUE (username),
    CONSTRAINT uk_member_email    UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 4-4-1. ORDERS
```sql
CREATE TABLE orders (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    product_name  VARCHAR(100) NOT NULL,
    price         BIGINT       NOT NULL,
    order_date    DATE         NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```


### 4-5. application.properties 연동 설정

`src/main/resources/application.properties`

```properties
spring.application.name=assignment

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/goodit?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=goodit
spring.datasource.password=admin

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.jpa.hibernate.ddl-auto=validate

spring.jpa.open-in-view=false
logging.level.kr.goodit=DEBUG

spring.thymeleaf.cache=false
spring.thymeleaf.encoding=UTF-8
```

`ddl-auto=validate`를 사용하므로 **schema.sql 실행이 애플리케이션 최초 기동보다 반드시 먼저** 이루어져야 합니다. 테이블이 없거나 컬럼 정의가 엔티티와 다르면 기동 시 `SchemaManagementException`이 발생합니다.

## 5. 빌드

저장소 클론 (EC2 서버 내부에서 직접 빌드하는 경우):

```bash
git clone https://github.com/Clarus23/goodit.git
cd goodit
```

`build.gradle`의 `dependencies`에 다음이 포함되어 있는지 확인합니다.

```gradle
runtimeOnly 'com.mysql:mysql-connector-j'
```

Gradle Wrapper로 빌드합니다 (테스트 제외).

```bash
chmod +x gradlew
./gradlew clean build -x test
```

빌드 결과는 `build/libs/` 아래에 두 개의 jar로 생성됩니다.

```bash
ls build/libs/
# assignment-0.0.1-SNAPSHOT.jar         <- 실행 가능 jar (이것을 사용)
# assignment-0.0.1-SNAPSHOT-plain.jar   <- 실행 불가능, 클래스 파일만 포함 (사용하지 않음)
```

## 6. 실행

### 6-1. 단독 실행 (테스트용)

```bash
java -jar build/libs/assignment-0.0.1-SNAPSHOT.jar
```


### 6-2. systemd 서비스로 실행 

배포 디렉토리 구성 및 전용 실행 계정 생성:

```bash
sudo useradd -r -s /bin/false appuser
sudo mkdir -p /opt/goodit
sudo cp build/libs/assignment-0.0.1-SNAPSHOT.jar /opt/goodit/app.jar
sudo chown -R appuser:appuser /opt/goodit
```

`/etc/systemd/system/goodit.service`

```ini
[Unit]
Description=Goodit Assignment Spring Boot Service
After=network.target mysql.service

[Service]
User=appuser
Group=appuser
WorkingDirectory=/opt/goodit
ExecStart=/usr/bin/java -jar /opt/goodit/app.jar
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

StandardOutput=journal
StandardError=journal
SyslogIdentifier=goodit

[Install]
WantedBy=multi-user.target
```

서비스 등록 및 기동:

```bash
sudo systemctl daemon-reload
sudo systemctl start goodit
sudo systemctl enable goodit
sudo systemctl status goodit
```

### 6-3. nginx 리버스 프록시

```bash
sudo apt install nginx -y
```

`/etc/nginx/sites-available/goodit`

```nginx
server {
    listen 80;
    server_name 52.79.250.9;

    access_log /var/log/nginx/goodit_access.log;
    error_log /var/log/nginx/goodit_error.log;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
sudo ln -s /etc/nginx/sites-available/goodit /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl reload nginx
sudo systemctl enable nginx
```

## 7. 운영 스크립트

`/opt/goodit/scripts/`에 위치하며, 모두 `chmod +x`로 실행 권한이 부여되어 있습니다.

| 스크립트 | 설명 |
|---|---|
| `start.sh` | goodit 서비스 시작 및 상태 출력 |
| `stop.sh` | goodit 서비스 중지 및 결과 확인 |
| `restart.sh` | goodit 서비스 재시작 |
| `log.sh [줄 수]` | 최근 로그 출력 (기본 50줄) |
| `deploy.sh` | git pull → 빌드 → 서비스 재배포 일괄 처리 |

사용 예시:

```bash
/opt/goodit/scripts/start.sh
/opt/goodit/scripts/log.sh 100
```

## 8. API 엔드포인트
> 인증이 필요한 API는 먼저 `/login`으로 로그인해 세션 쿠키를 발급받아야 합니다.

```bash
# 1. 회원가입
curl -s -X POST http://localhost/api/v1/members \
  -H "Content-Type: application/json" \
  -d '{"username":"tester","password":"password123","email":"tester@test.com"}'

# 2. 로그인 (세션 쿠키를 cookies.txt에 저장)
curl -s -c cookies.txt -X POST http://localhost/login \
  -d "username=tester&password=password123"

# 3. 인증 필요 API 호출 (저장된 쿠키 사용)
curl -s -b cookies.txt http://localhost/api/v1/orders/stats
```

### 엔드포인트 목록

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| POST | /api/v1/members | 불필요 | 회원 가입 (201) |
| GET | /api/v1/members | 필요 | 전체 회원 조회 |
| GET | /api/v1/members/{id} | 필요 | 단건 회원 조회 |
| GET | /api/v1/orders/stats | 필요 | 최근 7일 주문 통계 |

### 응답 형식

모든 API 응답은 아래 형식으로 통일됩니다.

```json
// 성공
{ "success": true, "data": { ... }, "message": null, "errors": null }

// 실패
{ "success": false, "data": null, "message": "에러 메시지", "errors": null }
```

### 주문 통계 응답 예시

```bash
curl -s -b cookies.txt http://localhost/api/v1/orders/stats
```

```json
{
  "success": true,
  "data": {
    "totalAmount": 3595000,
    "totalOrderCount": 14,
    "dailySales": [
      { "orderDate": "2026-06-12", "totalAmount": 400000, "orderCount": 1 },
      { "orderDate": "2026-06-13", "totalAmount": 215000, "orderCount": 2 }
    ]
  },
  "message": null,
  "errors": null
}
```

## 9. 로그 확인

```bash
# 애플리케이션 실시간 로그
sudo journalctl -u goodit -f

# 최근 100줄
sudo journalctl -u goodit -n 100 --no-pager

# nginx 접속/에러 로그
sudo tail -f /var/log/nginx/goodit_access.log
sudo tail -f /var/log/nginx/goodit_error.log
```

## 10. 동작 확인 체크리스트

```bash
sudo systemctl status mysql   # active (running)
sudo systemctl status goodit  # active (running)
sudo systemctl status nginx   # active (running)

curl -v http://localhost:8080        # 302 -> /login 리다이렉트 확인
curl -v http://localhost:8080/login  # 200, 로그인 폼 HTML 확인
```

브라우저에서 `http://52.79.250.9/login` 접속 시 로그인 화면이 표시되면 nginx → Spring Boot → MySQL까지 전체 연동이 정상입니다.

재부팅 후 자동 기동 여부도 확인하는 것을 권장합니다.

```bash
sudo reboot
# 재접속 후
sudo systemctl status mysql goodit nginx
```
