FROM amazoncorretto:11-alpine-jdk
# WORKDIR /Java/shareit/gateway
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
