FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY common-events/pom.xml common-events/pom.xml
COPY order-service/pom.xml order-service/pom.xml
COPY catalog-service/pom.xml catalog-service/pom.xml
COPY inventory-service/pom.xml inventory-service/pom.xml
COPY payment-service/pom.xml payment-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY common-events common-events
COPY order-service order-service
COPY catalog-service catalog-service
COPY inventory-service inventory-service
COPY payment-service payment-service
COPY notification-service notification-service
ARG SERVICE
RUN mvn -B -pl ${SERVICE} -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG SERVICE
COPY --from=build /workspace/${SERVICE}/target/${SERVICE}-1.0.0.jar app.jar
RUN addgroup -S commerce && adduser -S commerce -G commerce
USER commerce
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
