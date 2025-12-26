# =========================
# Build stage (Java 25 + Maven)
# =========================
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Install Maven manually
RUN apt-get update \
    && apt-get install -y maven \
    && mvn -version \
    && java -version

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source
COPY src ./src

# Build with Java 25
RUN mvn clean package -DskipTests

# =========================
# Runtime stage (Java 25)
# =========================
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
