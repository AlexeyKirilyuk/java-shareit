FROM amazoncorretto:11-alpine-jdk
# WORKDIR  /Java/shareit/server
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
#ENTRYPOINT ["java","-jar","/target/server-image.jar"]