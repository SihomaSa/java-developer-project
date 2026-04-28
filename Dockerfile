# ===========================
# STAGE 1: Build
# ===========================
FROM maven:3.9.5-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copiar pom.xml primero para cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar fuentes y compilar
COPY src ./src
RUN mvn package -DskipTests -B

# ===========================
# STAGE 2: Runtime
# ===========================
FROM eclipse-temurin:17-jre-alpine

# Usuario no-root (buena práctica de seguridad)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copiar JAR desde builder
COPY --from=builder /app/target/*.jar app.jar

# Variables de entorno configurables
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

EXPOSE 8080

# Health check para Docker/Kubernetes
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
