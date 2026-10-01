# PayFlow Payment Service

A Spring Boot microservice for creating, querying, and cancelling payment records. It exposes a REST API backed by PostgreSQL and uses Flyway for schema management. The service also includes OpenAPI documentation via Springdoc.

## Features

- Create payment records with validation
- Fetch a single payment by ID
- List payments with pagination and sorting
- Cancel payments when allowed by state rules
- Persist payment data in PostgreSQL
- Automatic database migrations with Flyway
- Swagger UI for local API exploration

## Tech Stack

- Java 21
- Spring Boot 3 / Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Flyway
- Spring Validation
- Springdoc OpenAPI

## Project Structure

```text
src/
  main/
    java/com/payflow/payment/
      controller/
      dto/
      entity/
      exception/
      repository/
      service/
      PaymentServiceApplication.java
    resources/
      application.properties
      db/
```

## Prerequisites

- Java 21+
- Maven or the included Maven wrapper (`./mvnw`)
- PostgreSQL running locally or in a reachable environment

## Configuration

Update the datasource settings in `src/main/resources/application.properties` before running the app:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/payflow
spring.datasource.username=postgres
spring.datasource.password=your_password
```

Make sure the database exists and is accessible from the application.

## Run the Service

From the project root:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

The service will start on:

- http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## API Summary

Base path: `/api/payments`

### Create payment

```http
POST /api/payments
Content-Type: application/json
```

Example body:

```json
{
  "sourceAccount": "ACC-1001",
  "destinationAccount": "ACC-2002",
  "amount": 125.50,
  "currency": "USD"
}
```

### Get payment by ID

```http
GET /api/payments/{id}
```

### List payments

```http
GET /api/payments?page=0&size=20&sort=createdAt,desc
```

### Cancel payment

```http
POST /api/payments/{id}/cancel
```

## Payment States

Payments can be in the following states:

- `PENDING`
- `COMPLETED`
- `FAILED`
- `CANCELLED`

## Notes

- Payment amounts are validated as positive values.
- Currency values are expected to be ISO 3-letter codes.
- Payment references are generated automatically in the format `PAY-...`.

## Testing

```bash
./mvnw test
```
