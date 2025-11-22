# Technology Stack

## Current Setup

**Language:** Java 21  
**Framework:** Spring Boot 3.5.7  
**Build Tool:** Gradle 9.1.0 (Groovy DSL)  
**CLI Framework:** Spring Shell 3.3.3  
**JSON Processing:** Gson 2.10.1  
**JSON Schema Validation:** json-schema-validator 1.0.87  
**Testing Framework:** JUnit Jupiter 5.10.1  
**Property-Based Testing:** jqwik 1.8.2

## Project Structure

```
src/
├── main/
│   ├── java/com/usecaseassistant/
│   │   ├── cli/          # CLI interface layer
│   │   ├── domain/       # Domain models
│   │   ├── service/      # Application services
│   │   └── storage/      # Storage and serialization
│   └── resources/
└── test/
    └── java/com/usecaseassistant/
```

## Configuration

- Kiro MCP integration is disabled (see `.vscode/settings.json`)
- Java toolchain: Java 21
- Gradle wrapper included for consistent builds

## Common Commands

### Build
```bash
./gradlew build
```

### Test
```bash
./gradlew test
```

### Run
```bash
./gradlew bootRun
```

### Run as JAR
```bash
./gradlew bootJar
java -jar build/libs/use-case-assistant-1.0.0.jar
```

### Clean
```bash
./gradlew clean
```

### Install Dependencies
```bash
./gradlew dependencies
```

## Key Dependencies

**Spring Boot** - Application framework providing dependency injection, configuration management, and testing support  
**Spring Shell** - Interactive shell framework with command annotations, auto-completion, and built-in help  
**Gson** - JSON serialization/deserialization  
**json-schema-validator** - Validate JSON against schemas  
**jqwik** - Property-based testing library for Java (QuickCheck-style)

## Testing Strategy

- **Unit tests** using JUnit Jupiter for specific examples and edge cases
- **Property-based tests** using jqwik for universal correctness properties
- Tests run with `./gradlew test` and include both JUnit and jqwik tests
- Property tests configured to run minimum 100 iterations per property
