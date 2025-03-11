# HobbyMatch Backend

## Overview
The backend of HobbyMatch is a RESTful API built with Kotlin and Spring Boot. It provides functionalities for user management, event handling, venue booking, and post creation. The application is containerized using Docker for easy deployment.

## Features
- User authentication and management
- Event creation, editing, and participation
- Venue management and reservations
- Posting system for users to share activities
- Admin functionalities for content moderation

## Tech Stack
- **Language**: Kotlin
- **Framework**: Spring Boot
- **Database**: Microsoft SQL Server
- **Containerization**: Docker
- **Authentication**: JWT

## Setup Instructions

### Prerequisites
- JDK 17\+
- Docker \& Docker Compose
- Microsoft SQL Server (Local or Azure SQL)

### Running Locally

#### 1. Clone the Repository
```sh
git clone <repo-url>
cd HobbyMatch-backend
```

#### 2. Configure Environment Variables
Create a `.env` file in the project root with:
```env
DB_USERNAME=sa
DB_PASSWORD=YourStrong\@Passw0rd
JWT_SECRET=your_secret_key
```
Adjust values as needed.

#### 3. Run with Docker
```sh
docker-compose up --build
```
This will start the backend service along with a SQL Server database.

#### 4. Run Manually (Without Docker)
```sh
./gradlew bootRun
```
Ensure that Microsoft SQL Server is running and accessible with your credentials.

## API Endpoints
\*Not implemented yet\*

## Deployment
```sh
docker-compose up --build
```
Starts the backend with the SQL Server container.

## License
\*Not implemented yet\*