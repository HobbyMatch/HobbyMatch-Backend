# Docker Complete Rebuild Process

To completely reset your Docker environment and rebuild the project:

## 1. Stop and Clean Up Docker Resources

```bash
# Stop all running containers
docker-compose down

# Remove volumes to clear database
docker-compose down -v

# Remove any dangling images
docker system prune -a --volumes
```

## 2. Check Gradle Cache

```bash
# Clean Gradle cache
./gradlew clean
```

[//]: # (## 3. Update Application Properties)

[//]: # ()
[//]: # (Ensure these settings in `application.properties` or `application.yml`:)

[//]: # ()
[//]: # (```properties)

[//]: # (# Force database recreation)

[//]: # (spring.jpa.hibernate.ddl-auto=create)

[//]: # ()
[//]: # (# Disable caching)

[//]: # (spring.cache.type=none)

[//]: # (```)

[//]: # ()
[//]: # (## 4. Rebuild and Start)

[//]: # ()
[//]: # (```bash)

[//]: # (# Build fresh Docker images)

[//]: # (docker-compose build --no-cache)

[//]: # ()
[//]: # (# Start containers)

[//]: # (docker-compose up -d)

[//]: # (```)

[//]: # ()
[//]: # (## 5. Verify API Responses)

[//]: # ()
[//]: # (Check that the response format includes all expected fields using a tool like Postman or curl:)

[//]: # ()
[//]: # (```bash)

[//]: # (curl http://localhost:8080/api/activities | jq)

[//]: # (```)

[//]: # ()