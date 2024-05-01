FROM maven:3-amazoncorretto-17-debian-bullseye as builder
LABEL authors="brito"

WORKDIR /build

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=builder /build/target/*.jar api.jar

EXPOSE 8080

CMD ["java", "-jar", "api.jar"]
