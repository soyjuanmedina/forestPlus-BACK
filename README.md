# ForestPlus Backend

Rest API and core logic for the ForestPlus reforestation platform. This project provides the data services and business logic for both ForestPlus Frontend V1 and V2.

## 🚀 Quick Start

### Prerequisites
- **Java 17** (OpenJDK or Amazon Corretto).
- **Maven 3.x** (or use the provided `./mvnw` wrapper).
- **MariaDB 10.x** (running locally or accessible via network).

### Environment Setup
1. Clone the repository.
2. Configure your database connection in `src/main/resources/application.yml` (or via environment variables).
3. Ensure the MariaDB instance is running and the database `forestplus` exists.

### Running the Application
```bash
./mvnw spring-boot:run
```

The server will start at `http://localhost:8080/api`.

## 🛠 Tech Stack
- **Spring Boot 3.5.6**: Core framework.
- **Spring Security + JWT**: Stateless authentication.
- **Spring Data JPA**: Database persistence.
- **MariaDB**: Relational database.
- **MapStruct**: Type-safe bean mapping.
- **Lombok**: Boilerplate reduction.
- **SpringDoc OpenAPI**: Automatic Swagger documentation.

## 📖 API Documentation
Once the server is running, you can access the interactive API documentation at:
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs`

## 🏗 Project Structure
- `src/main/java/com/forestplus/controller`: REST endpoints.
- `src/main/java/com/forestplus/service`: Business logic layer.
- `src/main/java/com/forestplus/entity`: JPA domain models.
- `src/main/java/com/forestplus/dto`: Data Transfer Objects (Requests/Responses).
- `src/main/java/com/forestplus/mapper`: MapStruct interfaces for DTO/Entity conversion.

## 🤖 AI & Developer Context
For building new features and understanding internal patterns, please refer to the [AI_CONTEXT.md](./AI_CONTEXT.md) file. This document contains detailed architectures and guidelines optimized for AI-assisted development.

## 📄 License
Check with the project owner for licensing information.
