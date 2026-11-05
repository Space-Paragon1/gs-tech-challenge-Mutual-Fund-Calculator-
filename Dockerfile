# Builds the full app (Angular website + Spring Boot API) into one image,
# so the site and the API are served together from a single URL.
# Used for deployment (see render.yaml). Local development still uses
# `mvn spring-boot:run` and `npm start`.

# 1. Build the Angular website
FROM node:22-alpine AS frontend
WORKDIR /frontend
ENV NG_CLI_ANALYTICS=false
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# 2. Build the Spring Boot jar with the website bundled in as static files
FROM maven:3.9.11-eclipse-temurin-25-alpine AS backend
WORKDIR /app
COPY pom.xml ./
COPY src ./src
COPY --from=frontend /frontend/dist/mutual-fund-calculator/ ./src/main/resources/static/
RUN mvn -B -q package -DskipTests

# 3. Run it
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=backend /app/target/tech-challenge-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
