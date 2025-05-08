# Base image Java 17
FROM eclipse-temurin:17-jdk-alpine

# Tạo thư mục và copy file jar
WORKDIR /app
COPY target/ApiWebReview-*.jar app.jar

# Lệnh chạy
ENTRYPOINT ["java", "-jar", "app.jar"]
