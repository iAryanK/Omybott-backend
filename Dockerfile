# Build stage
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw

COPY src src

RUN ./mvnw -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:25-jre

WORKDIR /app

RUN useradd --system --create-home --shell /usr/sbin/nologin appuser

COPY --from=build /app/target/omybott-*.jar app.jar
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh && chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh"]
