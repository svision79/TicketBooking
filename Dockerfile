FROM java:8-jdk-alpine
COPY /target/NearProjects-1.0.jar /usr/app/
COPY src/main/resources/function.properties /usr/app/
WORKDIR /usr/app
EXPOSE 27017
EXPOSE 8080
ENTRYPOINT ["java","-jar", "NearProjects-1.0.jar"]