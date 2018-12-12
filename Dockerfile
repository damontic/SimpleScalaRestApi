FROM openjdk:8-jre-alpine

COPY target/scala-2.12/SimpleScalaRestApi_2.12-0.1.0-SNAPSHOT.jar SimpleScalaRestApi_2.12-0.1.0-SNAPSHOT.jar

CMD ["java", "-jar", "SimpleScalaRestApi_2.12-0.1.0-SNAPSHOT.jar"]
