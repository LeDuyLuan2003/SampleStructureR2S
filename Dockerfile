# Sử dụng image JDK để chạy app
FROM eclipse-temurin:17-jdk

# Tạo thư mục làm việc trong container
WORKDIR /app

# Copy file jar vào container
COPY target/ApiWebReview-0.0.1-SNAPSHOT.jar app.jar

# Mở port ứng dụng
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]