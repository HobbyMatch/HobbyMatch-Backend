# Project Structure

This document explains the structure of the project, detailing which parts of the code should be in which directories for both code and tests.

## Source Code

The source code is organized under the `src/main/kotlin` directory. Here is a breakdown of the main directories and their purposes:

- **`controller`**: Contains the REST controllers that handle HTTP requests and responses.
    - Example: `HelloController.kt`

- **`service`**: Contains the service layer classes that contain business logic.
    - Example: `UserService.kt`

- **`repository`**: Contains the repository interfaces for data access.
    - Example: `UserRepository.kt`

- **`model`**: Contains the data models or entities.
    - Example: `User.kt`

- **`dto`**: Contains data transfer objects (dtos) which are used to send information in HTTP requests.
    - Example: `UserDto.kt`

## Test Code

The test code is organized under the `src/test/kotlin` directory. Here is a breakdown of the main directories and their purposes:

- **`unit`**: Contains unit tests.
    - Example: `HelloControllerTest.kt`

- **`integration`**: Contains integration tests that test multiple layers together.
    - Example: `UserIntegrationTest.kt`

## Resources

The resources are organized under the `src/main/resources` and `src/test/resources` directories.

- **`application.properties`**: Main application configuration.
- **`application-test.properties`**: Test-specific configuration.
- **`application-prod.properties`**: Production-specific configuration.
