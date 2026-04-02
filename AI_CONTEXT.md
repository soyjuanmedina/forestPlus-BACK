# AI Context: ForestPlus Backend (Ultra-Detailed)

This document is the "Source of Truth" for AI agents and developers working on the `forestPlus-BACK` project. It contains the essential knowledge to maintain consistency across the codebase.

## 1. Project Signature & Role
- **Core Role**: Centralized REST API for the ForestPlus reforestation platform.
- **Related Repos**: `forestPlus-FRONT` (V1), `forestPlus-FRONT-v2` (V2).
- **Communication Protocol**: JSON over HTTP, Stateless (JWT).

## 2. Technical Specs
- **Software Stack**:
  - **Java**: 17 (Amazon Corretto / OpenJDK).
  - **Framework**: Spring Boot 3.5.6.
  - **ORM/JPA**: Hibernate / Spring Data JPA.
  - **Database**: MariaDB 10.x.
  - **Mappers**: MapStruct 1.5.5.
  - **Security**: Spring Security + JJWT 0.11.5.
  - **Build**: Maven (`mvnw`).

## 3. Directory Structure & Layers
```text
src/main/java/com/forestplus/
├── config/       # Spring Configuration (CORS, Security, etc.)
├── controller/   # REST Endpoints (@RestController)
├── dto/
│   ├── request/  # Input DTOs
│   └── response/ # Output DTOs
├── entity/       # JPA Entities (@Entity)
├── mapper/       # MapStruct Interfaces
├── repository/   # Spring Data Repositories (@Repository)
├── service/      # Business logic (Interface + ServiceImpl)
└── security/     # JWT Token filtering and Auth logic
```

## 4. Domain Knowledge (Key Entities)

### User Management
- **UserEntity**: Stores credentials (email/hash), role, and metadata (login counts, email verification).
- **Roles**: `ADMIN` (Full access), `COMPANY_ADMIN` (Manage own company users), `USER` (Own trees/lands).

### Reforestation & Lands
- **LandEntity**: Defines a physical area. Geometrically defined by `CoordinateEntity`.
- **TreeTypeEntity**: Defines species/categories of trees (e.g., Oak, Pine) with its CO2 capture stats.
- **TreeEntity**: An individual tree instance. Can belong to a `User` or a `Company`. Linked to a `PlannedPlantation`.
- **PlannedPlantationEntity**: A project inside a Land for a specific TreeType.

## 5. Development Patterns

### The Mapper Pattern (Mandatory)
We use MapStruct for all Entity <-> DTO conversions.
```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "companyName", source = "company.name")
    UserResponseDto toDto(UserEntity entity);
}
```

### Response Convention
Always return `ResponseEntity<T>`. For lists, use `List<T>` or `Page<T>` (for heavy lists).
- Status 200: OK / Retrieval successful.
- Status 201: Created.
- Status 401/403: Security/Auth issues.
- Status 405/404: Not found / Method not allowed.

### Validation
Use `@Valid` and standard `jakarta.validation` annotations (e.g., `@NotBlank`, `@Size`, `@Email`) in Request DTOs.

## 6. API Surface (Major Endpoints)
- `/api/auth/**`: Login, Register, Password Reset.
- `/api/users/**`: CRUD for users, profile management.
- `/api/trees/**`: Individual tree tracking, batch planting.
- `/api/lands/**`: Land management and mapping data.
- `/api/companies/**`: Corporate CO2 tracking and members.

## 7. Security Details
- **Token**: JWT stored in `Authorization: Bearer <token>` header.
- **Secret**: Managed via environment variables or `application.yml`.
- **Interceptor**: `JwtAuthenticationFilter` validates every request.

## 8. AI Guidelines
- **DTOs**: Never expose Entities directly. Always use a Response DTO.
- **Lombok**: Use `@Data`, `@Builder`, `@NoArgsConstructor`, and `@AllArgsConstructor` in entities and DTOs.
- **Transactional**: Use `@Transactional` in Services for database writes.
- **Exception Handling**: Global exception handler is in `com.forestplus.exception`.
