# Backend Version 2 - Training Center Management System

## 📋 Giới thiệu

Hệ thống quản lý trung tâm đào tạo được xây dựng bằng Spring Boot 3.5.6, cung cấp các tính năng quản lý học viên, điểm
danh, đăng ký, giải đấu, thành tích và nhiều hơn nữa.

## 🚀 Công nghệ sử dụng

### Core Technologies

- **Java**: 21
- **Spring Boot**: 3.5.6
- **Maven**: Build tool
- **PostgreSQL**: Database chính
- **Redis**: Caching và session management
- **RabbitMQ**: Message broker cho async processing

### Spring Framework

- Spring Data JPA
- Spring Data REST
- Spring Security
- Spring OAuth2 (Client & Resource Server)
- Spring Validation
- Spring Web & Web Services
- Spring Data Redis
- Spring AMQP
- Spring Actuator

### Security & Authentication

- JWT (JSON Web Tokens) - `io.jsonwebtoken:jjwt` v0.12.6
- OAuth2 Client & Resource Server
- Spring Security

### Other Libraries

- **Lombok**: Giảm boilerplate code
- **Jackson**: JSON processing v2.19.2
- **Lettuce**: Redis client v6.8.1
- **Dotenv Java**: Environment variables management v3.0.0

## 📁 Cấu trúc dự án

```
src/main/java/com/dat/backend_version_2/
├── config/              # Cấu hình (Security, Redis, RabbitMQ, CORS, etc.)
├── consumer/            # RabbitMQ consumers
├── controller/          # REST API endpoints
│   ├── achievement/     # Quản lý thành tích
│   ├── attendance/      # Điểm danh (Student, Coach, Trial)
│   ├── authentication/  # Đăng nhập, đăng ký
│   ├── authz/          # Authorization
│   ├── registration/    # Đăng ký khóa học
│   ├── tournament/      # Quản lý giải đấu
│   ├── training/        # Quản lý đào tạo
│   └── upload/         # Upload files
├── domain/             # Entities/Models
│   ├── achievement/
│   ├── attendance/
│   ├── authentication/
│   ├── authz/
│   ├── content/
│   ├── finance/
│   ├── marketing/
│   ├── notification/
│   ├── registration/
│   ├── tournament/
│   └── training/
├── dto/                # Data Transfer Objects
├── enums/              # Enumerations
├── listener/           # Event listeners
├── mapper/             # Entity-DTO mappers
├── producer/           # RabbitMQ producers
├── redis/              # Redis repositories
├── repository/         # JPA repositories
├── service/            # Business logic
├── specification/      # JPA specifications for queries
└── util/               # Utility classes
```

## ⚙️ Cấu hình

### Biến môi trường

Tạo file `.env` trong thư mục gốc với các biến sau:

```properties
# JWT
JWT_BASE64_SECRET=TKeq+EY8YJvX3hDTlt6Wor8TxGyYDYSs5Nieew3VAzL0G9XQcNFFJNOmIv2isY2ol8F9d29wnbdD7azzM0oqFQ==
JWT_ACCESS_TOKEN_VALIDITY_IN_SECONDS=86400
JWT_REFRESH_TOKEN_VALIDITY_IN_SECONDS=2592000
# Password Policy
TIME_PASSWORD_CHANGE_DAYS=90
# Bytescale (File Upload)
BYTESCALE_ACCOUNT_ID=kW2K8fv
BYTESCALE_API_KEY=secret_kW2K8fv6edXkqG7Tj91PRv4hXGaZ
# Server
PORT=8080
```

### Database Configuration

Ứng dụng sử dụng PostgreSQL với Hibernate:

- **DDL Auto**: `update` (tự động cập nhật schema)
- **Show SQL**: `true` (hiển thị SQL queries)
- **Timezone**: Asia/Ho_Chi_Minh

### Redis Configuration

- Sử dụng cho caching với TTL:
    - 1 ngày: 86400 seconds
    - 1 tuần: 604800 seconds
    - 1 tháng: 2592000 seconds

### HikariCP Connection Pool

```yaml
maximum-pool-size: 3
minimum-idle: 0
idle-timeout: 300000 (5 phút)
max-lifetime: 1800000 (30 phút)
connection-timeout: 30000 (30 giây)
```

## 🔧 Cài đặt và Chạy

### Yêu cầu tiên quyết

- **Java Development Kit (JDK)**: Bản 21 (LTS)
- **Docker Desktop**: Để chạy hạ tầng Database và Middleware.
- **Maven**: 3.6+
- **Ngrok**: Để public API cho Mobile App (nếu chạy trên thiết bị thật).

### Bước 1: Khởi động Hạ tầng (Infrastructure)

Dự án sử dụng Docker để quản lý Database và Message Queue.

1. Mở Docker Desktop.
2. Chạy các container sau (hoặc sử dụng file `docker-compose.yml` nếu có):

- **PostgreSQL**: Port 5432 (Database tên: `taekwondo_db`)
- **Redis**: Port 6379
- **RabbitMQ**: Port 5672 (Management UI: 15672)

*Lưu ý: Đảm bảo Restore dữ liệu vào PostgreSQL nếu có file backup `.sql`.*

### Bước 2: Clone và Cài đặt dependencies

```bash
git clone <repository-url>
cd backend_version_2
mvnw clean install
```

### Bước 3: Chạy ứng dụng Backend

```bash
# Development mode
mvnw spring-boot:run

# Hoặc build và chạy JAR
mvnw clean package
java -jar target/backend_version_2-0.0.1-SNAPSHOT.jar
```

Ứng dụng sẽ chạy tại: `http://localhost:8080`

### Bước 4: Public API (Bắt buộc nếu dùng Mobile App trên thiết bị thật)

Để ứng dụng React Native trên điện thoại có thể gọi API, cần sử dụng Ngrok để tạo tunnel:

Mở terminal mới.

Chạy lệnh: ngrok http 8080

Copy đường dẫn https (Ví dụ: https://fcd4ab59656f.ngrok-free.app).

Dán đường dẫn này vào file .env của Frontend Project (EXPO_PUBLIC_API_URL).

## 📡 API Endpoints

### Authentication

- `POST /api/v1/auth/login` - Đăng nhập
- `POST /api/v1/auth/register` - Đăng ký
- `POST /api/v1/auth/refresh` - Refresh token
- `POST /api/v1/auth/logout` - Đăng xuất

### Attendance (Điểm danh)

- Student Attendance
- Coach Attendance
- Trial Attendance

### Registration

- Quản lý đăng ký khóa học

### Tournament

- Quản lý giải đấu và thi đấu

### Achievement

- Quản lý thành tích học viên

### Training

- Quản lý lớp học và khóa đào tạo

### Health Check

- `GET /api/v1/health` - Kiểm tra trạng thái ứng dụng

### Upload

- Upload và quản lý files (sử dụng Bytescale)

## 🔐 Security

- **JWT Authentication**: Access token và refresh token
- **OAuth2**: Support OAuth2 client và resource server
- **CORS**: Đã cấu hình CORS policy
- **Password Policy**: Yêu cầu đổi mật khẩu định kỳ
- **Spring Security**: Bảo mật endpoints

## 📊 Monitoring

- **Spring Actuator**: Cung cấp health checks và metrics
- **Logging**: Cấu hình log levels cho từng component

## 🔄 Message Queue

Sử dụng RabbitMQ cho:

- Xử lý điểm danh bất đồng bộ (Student Attendance Consumer)
- Event-driven architecture
- Decoupling services

## 🗄️ Caching Strategy

- Redis caching cho dữ liệu thường xuyên truy cập
- TTL-based cache expiration
- Support cho distributed caching

## 🌍 Timezone

Hệ thống sử dụng timezone: **Asia/Ho_Chi_Minh** (GMT+7)

## 📝 Logging Levels

```yaml
Spring Data Repository: ERROR
Spring Security: WARN
HikariCP: WARN
Hibernate: WARN
PostgreSQL: WARN
Spring Web: DEBUG
Root: INFO
```

## 🧪 Testing

## 📦 Build Production

```bash
# Build JAR file
mvnw clean package -DskipTests

# JAR file sẽ được tạo tại: target/backend_version_2-0.0.1-SNAPSHOT.jar
```

## 👥 Team

Developed by DAT Team

## 📞 Contact

0352232092

---

**Note**: Đảm bảo cấu hình đúng các biến môi trường trước khi chạy ứng dụng. Không commit file `.env` vào repository.

