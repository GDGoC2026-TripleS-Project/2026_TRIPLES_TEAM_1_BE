# Dockerfile

# jdk21 Image Start
FROM eclipse-temurin:21-jdk

# 인자 설정 부분과 jar 파일 복제 부분 합쳐서 진행해도 무방
COPY build/libs/*.jar app.jar

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar", "--server.address=0.0.0.0"]

# 컨테이너의 기본 시간대를 'Asia/Seoul'로 설정
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
