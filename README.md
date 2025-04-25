# 📦 SampleStructureR2S - Backend Spring Boot

Dự án Spring Boot mẫu theo chuẩn phân tầng  
Tích hợp xác thực người dùng với **JWT + Refresh Token bằng HttpOnly Cookie**

## 🚀 Tính năng chính

- Đăng ký / Đăng nhập bằng email & password
- Gửi email xác thực OTP or Link khi đăng kí
- Khi thiết bị lạ truy cập thông báo tới email
- Sinh `accessToken` và `refreshToken`
- Tự động gia hạn access token nếu còn `refreshToken` hợp lệ
- Hỗ trợ bảo mật với HttpOnly Cookie
- Cấu trúc rõ ràng, dễ mở rộng

---

## 📋 Yêu cầu hệ thống

- Java 17+
- Maven 3.8+
- MySQL 8.x
- IDE: IntelliJ / VSCode / Eclipse
---
  ## ⚙️ Setup
  
### 1. Clone dự án 
git clone https://github.com/LeDuyLuan2003/SampleStructureR2S.git
cd SampleStructureR2S

### 2. Cấu hình môi trường
#### Bước 1:
##### Tạo file .env trong thư mục gốc và điền thông tin như sau:
//JWT Configuration
JWT_SECRET=j83hf82nf92hf73hf84hf83h38f93hf7h38fh3f
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

//Database Configuration
DB_URL=jdbc:mysql://localhost:3306/ApiWebReview
DB_USERNAME=root
DB_PASSWORD=

//Mail Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME= example@gamil.com (email của bạn)
MAIL_PASSWORD=app-password( hướng dẫn lấy app-password của gmail, https://mona.host/huong-dan-lay-mat-khau-ung-dung-mail/)
CLIENT_URL=http://localhost:8080

#### Bước 2: Cấu hình Run Configuration trong IntelliJ
- Mở IntelliJ IDEA, đảm bảo đã mở project của bạn.
    - Trên thanh menu, chọn:
      - Run → Edit Configurations..
      - Trong cửa sổ cấu hình:
      - Chọn cấu hình ứng dụng Spring Boot của bạn (nếu chưa có, nhấn + → Spring Boot → chọn main class).
      - Ở phần "Environment variables", nhấn vào biểu tượng 📄 (góc phải ô input).
      - Import biến môi trường từ file .env:
      - Nhấn Import...
      - Chọn file .env trong thư mục gốc của project
      - Nhấn OK
      - Kiểm tra: Các biến như JWT_SECRET, JWT_EXPIRATION,... sẽ hiện ra trong danh sách.
      - Nhấn OK để lưu cấu hình và chạy lại app là được.

#### Bước 3:
💡 Bạn cần tạo sẵn database trước khi chạy app:
CREATE DATABASE ApiWebReview;


### 3: Run dự án
mvn clean install
mvn spring-boot:run
API sẽ khởi động tại: http://localhost:8080

### 🔐 Authentication Flow
API Endpoint | Mô tả

POST	/api/auth/register	Đăng ký tài khoản với fullname, email, password
POST	/api/auth/login	Đăng nhập, trả về accessToken và set cookie chứa refreshToken
POST	/api/auth/refresh	Làm mới accessToken bằng refreshToken trong cookie
POST	/api/auth/logout	Xoá refreshToken trong DB và cookie phía client
GET	/api/auth/verify?token=...	Xác minh tài khoản bằng url có chứa token gửi qua email (kiểu UUID)
GET	/api/auth/verify-otp?otp=...	Xác minh OTP (One-Time Password)

---

## Link postman:
https://www.postman.com/chatapp-7862/apiwebreview/overview

---

## Cấu trúc dự án:
'''bash
src/
└── main/
    └── java/
        └── com/
            └── r2s/
                └── ApiWebReview/
                    ├── common/
                    │   ├── annotation/         # Custom annotations
                    │   ├── constant/           # Hằng số toàn cục (AppConstants,SecurityConstants)
                    │   ├── enum/               # Enum dùng chung (Role, Status,...)
                    │   ├── event/              # App events (e.g., UserRegisteredEvent)
                    │   ├── response/           # Response model (ApiResponse, PagingResponse)
                    │   └── util/               # Helper class (e.g., TokenUtil, DateUtil)
                    │
                    ├── config/                # Cấu hình Spring (Security, Swagger, ...)
                    ├── controller/            # REST API endpoints
                    ├── dto/                   # Request / Response DTO
                    ├── entity/                # JPA entities
                    ├── exception/             # Global exception handler, custom exceptions
                    ├── mapper/                # DTO <-> Entity mapping
                    ├── repository/            # Spring Data JPA Repositories
                    ├── security/              # JWT, Spring Security filters
                    ├── service/               # Business logic
                    └── ApiWebReviewApplication.java  # Main Spring Boot class

