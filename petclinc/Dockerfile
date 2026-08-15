FROM maven:3.9.11-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

RUN apt-get update \
    && apt-get upgrade -y \
    && apt-get install -y --no-install-recommends ca-certificates tzdata \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system app \
    && useradd --system --gid app --create-home --home-dir /home/app app

WORKDIR /app

COPY --from=builder /app/target/petclinc-app-1.0.0.jar /app/app.jar

USER app

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]