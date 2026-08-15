FROM maven:3.9.11-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests

FROM gcr.io/distroless/java17-debian12:nonroot

WORKDIR /app

COPY --from=builder /app/target/petclinc-app-1.0.0.jar /app/app.jar

USER nonroot:nonroot

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]