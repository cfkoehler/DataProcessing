FROM maven:3-amazoncorretto-11 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn package
FROM amazoncorretto:11-alpine3.18-jdk
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/DpFeeder.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]