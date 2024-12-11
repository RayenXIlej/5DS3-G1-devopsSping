FROM openjdk:17-jdk-alpine
EXPOSE 8082
ADD target/devopsSpring_5DS3_G1-1.0.jar devopsSpring_5DS3_G1-1.0.jar
ENTRYPOINT ["java","-jar","/devopsSpring_5DS3_G1-1.0.jar"]