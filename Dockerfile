FROM openjdk
COPY /target/NearProjects-1.0.jar /usr/app/
COPY src/main/resources/function.properties /usr/app/src/main/resources/
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java","-jar", "NearProjects-1.0.jar"]