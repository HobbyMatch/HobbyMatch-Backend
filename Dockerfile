FROM eclipse-temurin:17-jdk as build
WORKDIR /workspace/app

# Copy gradle files first for better layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy detekt configuration
COPY config config

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached if gradle files don't change)
RUN ./gradlew dependencies

# Copy source code
COPY src src

# Build the application
RUN ./gradlew build -x test

# Runtime image
FROM eclipse-temurin:17-jre
VOLUME /tmp

# Copy the jar from the build stage
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Set entrypoint
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Default environment - can be overridden during docker run
ENV SPRING_PROFILES_ACTIVE=prod