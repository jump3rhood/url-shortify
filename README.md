# URL Shortener Project

## Setup Instructions
1. Clone the repository
2. Copy `application.properties.example` to `application.properties`
3. Update the following configurations in `application.properties`:
    - Database credentials
    - JWT secret (must be a Base64 encoded string)
4. Run the application

## Required Environment
- Java 17+
- MySQL 8+
- Maven

## Database Setup
1. Create a MySQL database named 'links'
2. Update database credentials in application.properties

## Running the Application
```bash
mvn spring-boot:run