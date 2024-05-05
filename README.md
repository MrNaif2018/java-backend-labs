# E-commerce store management

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=MrNaif2018_java-backend-labs&metric=alert_status)](https://sonarcloud.io/summary/overall?id=MrNaif2018_java-backend-labs)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MrNaif2018_java-backend-labs&metric=security_rating)](https://sonarcloud.io/summary/overall?id=MrNaif2018_java-backend-labs)

[Live demo](https://java-backend-labs-production.up.railway.app)

This API provides endpoints to create and manage an unlimited number of stores, products, frontend, as well as a payment processing integration with [Bitcart](https://bitcart.ai)

## Setup

This project uses PostgreSQL database to store it's internal state.

Installation steps (for ubuntu 22.04):

```bash
sudo apt install postgresql postgresql-contrib
```

You can configure database password and other connection settings in `src/main/resources/application.yml` (or via environment variables like `SPRING_DB_PASSWORD`)

By default, you need to have database named `java` present. You can create it using:

```bash
sudo -u postgres createdb java
```

The app will run database migrations on boot automatically

## Running

To run the app, start it from your IDE or use maven:

```bash
mvn spring-boot:test-run
```

You can also run it in docker using docker compose:

```bash
docker compose up -d
```

## Testing

You can run tests using your IDE or maven:

```bash
mvn test
```

## Used stack

- Java 21
- Spring Web 3.2.x series
- PostgreSQL 14
- Maven

## Usage

You can find swagger UI documentation at `http://localhost:8080` and try API requests here

![Swagger UI](./screenshots/swagger.png)
