# 📦 SampleStructureR2S - Backend Spring Boot

Dự án Spring Boot mẫu theo chuẩn phân tầng `Service / Repository / Controller`  
Tích hợp xác thực người dùng với **JWT + Refresh Token bằng HttpOnly Cookie**

## 🚀 Tính năng chính

- Đăng ký / Đăng nhập bằng email & password
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

1. Clone dự án 
```bash
git clone https://github.com/LeDuyLuan2003/SampleStructureR2S.git
cd SampleStructureR2S

2. Cấu hình môi trường
- Bước 1:
Tạo file .env trong thư mục gốc và điền thông tin như sau:
# JWT Configuration
JWT_SECRET=j83hf82nf92hf73hf84hf83h38f93hf7h38fh3f
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

### Database Configuration
DB_URL=jdbc:mysql://localhost:3306/ApiWebReview
DB_USERNAME=root
DB_PASSWORD=

Bước 2: Cấu hình Run Configuration trong IntelliJ
Mở IntelliJ
Chọn Run > Edit Configurations
Ở mục Environment variables, nhấn biểu tượng 📄 kế bên
Nhấn Import… > chọn file .env
Nhấn OK để lưu cấu hình
Bước 3:
💡 Bạn cần tạo sẵn database trước khi chạy app:
CREATE DATABASE ApiWebReview;


3: Run dự án
mvn clean install
mvn spring-boot:run


API sẽ khởi động tại: http://localhost:8080

🔐 Authentication Flow
API Endpoint | Mô tả
POST /api/auth/register | Đăng kí, với fullname , email và password
POST /api/auth/login | Đăng nhập, trả accessToken + set cookie chứa refreshToken
POST /api/auth/refresh | Làm mới accessToken từ refreshToken trong cookie
POST /api/auth/logout | Xoá refreshToken trong DB và xoá cookie phía client

Link postman:
https://www.postman.com/chatapp-7862/apiwebreview/overview
