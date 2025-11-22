# Use Case Assistant

A CLI application that guides users through creating, managing, and exporting use cases following Alistair Cockburn's methodology from "Writing Effective Use Cases".

## Project Structure

```
src/
├── main/
│   ├── java/com/usecaseassistant/
│   │   ├── cli/          # CLI interface layer (commands, prompts, display)
│   │   ├── domain/       # Domain models (UseCase, Scenario, Step, etc.)
│   │   ├── service/      # Application services (validation, orchestration)
│   │   └── storage/      # Storage and serialization (repository, export)
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/usecaseassistant/
        ├── cli/          # CLI layer tests
        ├── domain/       # Domain model tests
        ├── service/      # Service layer tests
        └── storage/      # Storage layer tests
```

## Technology Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.5.7
- **Build Tool:** Gradle 9.1.0
- **CLI Framework:** Spring Shell 3.3.3
- **JSON Processing:** Gson 2.10.1
- **JSON Schema Validation:** json-schema-validator 1.0.87
- **Testing:** JUnit Jupiter 5.10.1
- **Property-Based Testing:** jqwik 1.8.2

## Getting Started

### Prerequisites

- Java 21 or higher
- Gradle 9.1.0 (or use the included wrapper)

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

### Package as JAR

```bash
./gradlew bootJar
java -jar build/libs/use-case-assistant-1.0.0.jar
```

## Development

### Running Tests

The project includes both unit tests (JUnit) and property-based tests (jqwik):

```bash
# Run all tests
./gradlew test

# Run tests with detailed output
./gradlew test --info

# Run specific test class
./gradlew test --tests "com.usecaseassistant.UseCaseAssistantApplicationTest"
```

### Project Configuration

- Use cases are stored in: `~/.usecase-assistant/use-cases/`
- Configuration: `src/main/resources/application.properties`
- Build configuration: `build.gradle`

## Architecture

The application follows a layered architecture:

1. **CLI Interface Layer** - User interaction via Spring Shell
2. **Application Service Layer** - Business logic and orchestration
3. **Domain Model Layer** - Core entities and value objects
4. **Storage & Export Layer** - Persistence and formatting

## Documentation

- [Requirements](.kiro/specs/use-case-assistant/requirements.md)
- [Design](.kiro/specs/use-case-assistant/design.md)
- [Implementation Tasks](.kiro/specs/use-case-assistant/tasks.md)

## License

[Add license information]
