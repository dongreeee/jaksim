# 1. 베이스 이미지 (Java 17) - java 17 기반의 경량 Linux 이미지 사용
FROM openjdk:17-jdk-alpine

# 2. 작업 디렉토리 설정 - 컨테이너 안 작업 디렉토리 /app 설정
WORKDIR /app

# 3. Gradle 빌드 결과 JAR 파일 복사 - 로컬 Gradle 빌드 결과 JAR 파일을 컨테이너에 복사
COPY build/libs/*.jar app.jar

# 4. 컨테이너 시작 시 JAR 실행 - 컨테이너 시작 시 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# 5. 컨테이너 포트 노출 - 컨테이너 포트 8080 외부 연결 가능 표시 (Spring Boot 기본) ㅇㅇ
EXPOSE 8080
