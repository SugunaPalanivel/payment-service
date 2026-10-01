# PayFlow Payment Service

PayFlow is a Spring Boot-based payment service built as a cloud-native backend portfolio project.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Flyway
- Bean Validation
- OpenAPI / Swagger
- Maven

## Features

- Create payments
- Retrieve payment by ID
- Paginated payment retrieval
- Cancel payments
- Request validation
- Global exception handling
- Database migrations with Flyway

## API Endpoints

POST /api/payments

GET /api/payments/{id}

GET /api/payments?page=0&size=10

POST /api/payments/{id}/cancel

## Running Locally

1. Start PostgreSQL.
2. Create a database named `payflow`.
3. Configure database credentials in `application.properties`.
4. Run:

```bash
mvn spring-boot:run