
# ----- build stage -----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom and sources (build each microservice independently)
COPY pom.xml ./
# For multi-module parent poms, you can speed up with a dependency-only build:
RUN mvn -q -B -DskipTests=true dependency:go-offline

# Copy the actual service sources
COPY src ./src

# Build the service jar
RUN mvn -q -B -DskipTests=true package

# ----- runtime stage -----
FROM eclipse-temurin:21-jre AS runtime
ENV TZ=UTC JAVA_OPTS=""

# Create a non-root user
RUN useradd -ms /bin/bash appuser
USER appuser

WORKDIR /app
# Copy the fat jar (Spring Boot jars end up as target/*-SNAPSHOT.jar or similar)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
