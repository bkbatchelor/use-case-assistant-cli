# Design Document

## Overview

The Use Case Assistant is a command-line interface (CLI) application that guides users through creating, managing, and exporting use cases following Alistair Cockburn's methodology. The application provides an interactive experience with validation, helpful guidance, and persistent storage of use case documents.

The system is designed around a core domain model representing use cases and their components, with separate layers for CLI interaction, validation logic, storage, and export formatting. This separation allows for maintainability and potential future extensions (e.g., web interface, API).

## Architecture

The application follows a layered architecture:

```
┌─────────────────────────────────────┐
│         CLI Interface Layer         │
│  (Commands, Prompts, Display)       │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Application Service Layer      │
│  (Use Case Management, Validation)  │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│         Domain Model Layer          │
│  (Use Case, Actor, Scenario, etc.)  │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Storage & Export Layer         │
│  (File I/O, Serialization, Format)  │
└─────────────────────────────────────┘
```

### Key Architectural Decisions

1. **Separation of Concerns**: CLI logic is separated from domain logic, allowing the core use case model to be reused in different contexts
2. **Validation as First-Class Concern**: Validation rules are centralized and applied consistently across creation and editing workflows
3. **File-Based Storage**: Use cases are stored as individual JSON files for simplicity and human readability
4. **Immutable Domain Objects**: Use case objects are immutable once created, with modifications creating new instances

## Components and Interfaces

### CLI Interface Layer

**CommandHandler**
- Responsibilities: Parse command-line arguments, route to appropriate commands
- Key Methods:
  - `execute(args: string[]): void` - Main entry point
  - `showHelp(): void` - Display usage information

**InteractivePrompt**
- Responsibilities: Handle user input during interactive sessions
- Key Methods:
  - `promptText(message: string, guidance?: string): string`
  - `promptChoice(message: string, options: string[]): string`
  - `promptConfirm(message: string): boolean`
  - `promptSteps(): Step[]` - Collect scenario steps interactively

**DisplayFormatter**
- Responsibilities: Format use cases and lists for terminal display
- Key Methods:
  - `displayUseCase(useCase: UseCase): void`
  - `displayList(useCases: UseCase[]): void`
  - `displayError(message: string): void`
  - `displayValidationError(error: ValidationError): void`

### Application Service Layer

**UseCaseService**
- Responsibilities: Orchestrate use case operations, coordinate validation and storage
- Key Methods:
  - `createUseCase(interactive: boolean): UseCase`
  - `loadUseCase(id: string): UseCase`
  - `updateUseCase(id: string, updates: Partial<UseCaseData>): UseCase`
  - `deleteUseCase(id: string): void`
  - `listUseCases(): UseCase[]`
  - `exportUseCase(id: string, format: ExportFormat, path: string): void`

**ValidationService**
- Responsibilities: Validate use case components against Cockburn's rules
- Key Methods:
  - `validateTitle(title: string): ValidationResult`
  - `validateGoalLevel(level: string): ValidationResult`
  - `validateStep(step: string): ValidationResult`
  - `validateExtension(extension: Extension, scenario: Scenario): ValidationResult`
  - `validateSuccessGuarantee(guarantee: string): ValidationResult`
  - `validateUseCase(useCase: UseCase): ValidationResult`

### Domain Model Layer

**UseCase**
- Properties:
  - `id: string` - Unique identifier
  - `title: string` - Goal-oriented title
  - `primaryActor: string` - Main stakeholder
  - `goalLevel: GoalLevel` - Summary, UserGoal, or Subfunction
  - `designScope: string` - System boundary description
  - `trigger: string` - Event that initiates the use case
  - `preconditions: string[]` - Conditions before execution
  - `postconditions: string[]` - Conditions after execution
  - `successGuarantees: string[]` - Minimal guarantees on success
  - `mainScenario: Scenario` - Primary success path
  - `extensions: Extension[]` - Alternative flows
  - `stakeholders: string[]` - All interested parties

**Scenario**
- Properties:
  - `steps: Step[]` - Ordered list of actions

**Step**
- Properties:
  - `number: number` - Step number in sequence
  - `actor: string` - Who performs the action (actor name or "System")
  - `action: string` - What is done

**Extension**
- Properties:
  - `condition: string` - When this extension applies
  - `branchPoint: number` - Step number where this branches
  - `steps: Step[]` - Alternative steps

**GoalLevel** (enum)
- Values: `Summary`, `UserGoal`, `Subfunction`

**ValidationResult**
- Properties:
  - `valid: boolean`
  - `errors: ValidationError[]`

**ValidationError**
- Properties:
  - `field: string`
  - `message: string`
  - `example?: string` - Example of correct format

### Storage & Export Layer

**UseCaseRepository**
- Responsibilities: Persist and retrieve use cases from file system
- Key Methods:
  - `save(useCase: UseCase): void`
  - `load(id: string): UseCase`
  - `loadAll(): UseCase[]`
  - `delete(id: string): void`
  - `exists(id: string): boolean`

**Serializer**
- Responsibilities: Convert between domain objects and JSON
- Key Methods:
  - `serialize(useCase: UseCase): string`
  - `deserialize(json: string): UseCase`
  - `validateSchema(json: string): boolean`

**ExportFormatter**
- Responsibilities: Generate formatted documents for export
- Key Methods:
  - `formatAsText(useCase: UseCase): string`
  - `formatAsMarkdown(useCase: UseCase): string`

## Data Models

### UseCase JSON Schema

```json
{
  "id": "string (UUID)",
  "title": "string",
  "primaryActor": "string",
  "goalLevel": "Summary | UserGoal | Subfunction",
  "designScope": "string",
  "trigger": "string",
  "preconditions": ["string"],
  "postconditions": ["string"],
  "successGuarantees": ["string"],
  "stakeholders": ["string"],
  "mainScenario": {
    "steps": [
      {
        "number": "number",
        "actor": "string",
        "action": "string"
      }
    ]
  },
  "extensions": [
    {
      "condition": "string",
      "branchPoint": "number",
      "steps": [
        {
          "number": "number",
          "actor": "string",
          "action": "string"
        }
      ]
    }
  ]
}
```

### File Storage Structure

```
~/.usecase-assistant/
  ├── use-cases/
  │   ├── {uuid-1}.json
  │   ├── {uuid-2}.json
  │   └── ...
  └── config.json
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Use case data round-trip preservation

*For any* valid use case, serializing it to JSON and then deserializing should produce an equivalent use case with all fields preserved.
**Validates: Requirements 3.1, 3.2**

### Property 2: Step format validation

*For any* string representing a scenario step, the validation should correctly identify whether it follows subject-verb-object format with an actor perspective.
**Validates: Requirements 1.4, 2.2**

### Property 3: Title goal-orientation validation

*For any* use case title, the validation should correctly distinguish between goal-oriented titles (expressing what the user wants to achieve) and function-oriented titles (describing system features).
**Validates: Requirements 2.1**

### Property 4: Goal level enumeration validation

*For any* goal level string, the validation should accept only "Summary", "UserGoal", or "Subfunction" and reject all other values.
**Validates: Requirements 2.3**

### Property 5: Extension branch point validation

*For any* extension and main scenario, the validation should verify that the extension's branch point references an existing step number in the main scenario.
**Validates: Requirements 2.4**

### Property 6: Success guarantee format validation

*For any* success guarantee string, the validation should verify it is stated as a condition (describing a state) rather than an action (describing a process).
**Validates: Requirements 2.5**

### Property 7: Schema validation on load

*For any* JSON string, attempting to deserialize it should either produce a valid use case or fail with a clear schema validation error.
**Validates: Requirements 3.4**

### Property 8: File operation error reporting

*For any* file operation that fails (save, load, delete), the system should generate an error message that clearly describes what went wrong.
**Validates: Requirements 3.5, 5.5, 8.4**

### Property 9: List completeness and information

*For any* set of saved use cases, listing them should return all use cases with their title, primary actor, and goal level included in the display.
**Validates: Requirements 4.1, 4.2**

### Property 10: Alphabetical sorting

*For any* set of use cases with different titles, listing them should return them sorted alphabetically by title.
**Validates: Requirements 4.5**

### Property 11: List selection loads correct use case

*For any* use case in the list, selecting it should load the exact use case with all its original data intact.
**Validates: Requirements 4.4**

### Property 12: Export completeness

*For any* use case, exporting it should produce a document containing all required sections: title, actors, goal level, design scope, trigger, preconditions, postconditions, success guarantees, main scenario, and extensions, with clear section headers and proper indentation.
**Validates: Requirements 5.1, 5.2, 5.3**

### Property 13: Export file creation

*For any* valid file path and use case, exporting should create a file at the specified path containing the formatted use case.
**Validates: Requirements 5.4**

### Property 14: Edit field display

*For any* use case being edited, all current field values should be displayed to the user before modifications.
**Validates: Requirements 6.1**

### Property 15: Edit persistence

*For any* use case and valid modifications, completing the edit should save the updated use case to the same file location with all changes applied.
**Validates: Requirements 6.4**

### Property 16: Edit cancellation preservation

*For any* use case being edited, cancelling the edit should leave the original use case unchanged in storage.
**Validates: Requirements 6.5**

### Property 17: Validation consistency

*For any* use case field, the validation rules applied during editing should be identical to those applied during creation.
**Validates: Requirements 6.3**

### Property 18: Prompt guidance presence

*For any* user input prompt, guidance text explaining what is expected should be displayed along with the prompt.
**Validates: Requirements 7.1**

### Property 19: Validation error explanation

*For any* validation error, the error message should include both an explanation of why the input was rejected and an example of correct format.
**Validates: Requirements 7.2**

### Property 20: Deletion removes file

*For any* use case, confirming its deletion should remove the corresponding file from storage.
**Validates: Requirements 8.2**

### Property 21: Deletion cancellation preservation

*For any* use case, cancelling its deletion should leave the file unchanged in storage.
**Validates: Requirements 8.3**

## Error Handling

### Validation Errors

All validation errors should:
- Clearly identify which field failed validation
- Explain why the input was rejected
- Provide an example of correct format
- Not cause the application to crash or lose user data

### File System Errors

File system errors should:
- Be caught and converted to user-friendly messages
- Preserve existing data (never corrupt or partially write files)
- Indicate the specific operation that failed
- Suggest potential remedies when possible (e.g., check permissions, verify path exists)

### User Input Errors

User input errors should:
- Allow the user to retry without losing previous input
- Provide clear guidance on what went wrong
- Never expose internal implementation details

### Error Recovery

The application should:
- Validate data before writing to disk
- Use atomic file operations where possible
- Maintain backup copies during updates
- Allow users to cancel operations before committing changes

## Testing Strategy

### Unit Testing

Unit tests will cover:
- Individual validation functions with specific examples of valid and invalid inputs
- Serialization and deserialization of use case objects
- File path handling and error conditions
- Display formatting functions with sample use cases
- Edge cases like empty lists, missing files, and malformed JSON

### Property-Based Testing

Property-based tests will verify universal properties using a PBT library appropriate for the chosen language (e.g., fast-check for TypeScript/JavaScript, Hypothesis for Python, QuickCheck for Haskell).

**Configuration:**
- Each property test should run a minimum of 100 iterations
- Each property test must be tagged with a comment referencing the correctness property from this design document
- Tag format: `**Feature: use-case-assistant, Property {number}: {property_text}**`

**Property Test Coverage:**
- Each correctness property listed above must be implemented as a single property-based test
- Generators should create random but valid use cases, steps, extensions, and other domain objects
- Tests should verify that operations maintain invariants across all generated inputs

**Test Organization:**
- Property tests should be placed close to the implementation they validate
- Tests should be run as part of the standard test suite
- Failing property tests should output the specific input that caused the failure for debugging

### Integration Testing

Integration tests will verify:
- Complete workflows (create, save, load, edit, delete)
- CLI command parsing and routing
- End-to-end file operations
- Export generation and file writing

### Test Data

Test data should include:
- Well-formed use cases following Cockburn's methodology
- Malformed use cases with various validation errors
- Edge cases (empty scenarios, no extensions, minimal data)
- Large use cases with many steps and extensions

## Implementation Notes

### Technology Choices

The specific technology stack (language, frameworks, libraries) should be determined based on:
- Target user environment and deployment needs
- Developer familiarity and team preferences
- Availability of quality CLI libraries
- Property-based testing library support

### CLI Framework

Consider using established CLI frameworks that provide:
- Command parsing and routing
- Interactive prompts with validation
- Colored output and formatting
- Help text generation

### File Format

JSON is chosen for storage because:
- Human-readable for debugging
- Well-supported across languages
- Easy to validate with schemas
- Extensible for future additions

### Validation Rules

Validation rules should be:
- Centralized in the ValidationService
- Reusable across creation and editing
- Testable in isolation
- Documented with examples

### Future Extensions

The architecture supports future additions:
- Web-based interface using the same domain model
- Import from other formats (Markdown, Word)
- Export to additional formats (HTML, PDF)
- Use case templates and wizards
- Collaboration features (sharing, commenting)
- Integration with requirements management tools
