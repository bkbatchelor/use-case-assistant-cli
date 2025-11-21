# Requirements Document

## Introduction

The Use Case Assistant is a CLI application that guides users through creating well-structured use cases following Alistair Cockburn's methodology from "Writing Effective Use Cases". The tool helps users capture system goals, identify stakeholders, structure scenarios, and maintain consistency across use case documentation. It enforces best practices such as goal-oriented thinking, appropriate scope levels, and clear success/failure conditions.

## Glossary

- **Use Case Assistant**: The CLI application system being developed
- **User**: A person using the Use Case Assistant to create or manage use cases
- **Use Case**: A description of how a system interacts with actors to achieve a goal
- **Actor**: A role played by a person or external system interacting with the system under discussion
- **Primary Actor**: The stakeholder with the goal the use case is trying to satisfy
- **Goal Level**: The scope of a use case (Summary, User Goal, or Subfunction)
- **Design Scope**: The boundary of the system being designed, defining what is inside versus outside the system
- **Main Success Scenario**: The primary path through a use case when everything succeeds
- **Extension**: An alternative path or error condition that branches from the main scenario
- **Precondition**: A condition that must be true before the use case can start
- **Postcondition**: A condition that describes the state of the system after the use case completes
- **Success Guarantee**: A condition that must be true after successful completion of the use case
- **Stakeholder**: Any person or system with an interest in the behavior of the system
- **Trigger**: The event that initiates the use case and causes the primary actor to start the interaction

## Requirements

### Requirement 1

**User Story:** As a user, I want to create a new use case interactively, so that I can document system behavior following Cockburn's methodology.

#### Acceptance Criteria

1. WHEN a user initiates use case creation, THE Use Case Assistant SHALL prompt for the use case title in goal-oriented format
2. WHEN a user provides use case details, THE Use Case Assistant SHALL collect primary actor, goal level, design scope, trigger, preconditions, postconditions, and success guarantees
3. WHEN a user completes the initial details, THE Use Case Assistant SHALL guide the user through defining the main success scenario step by step
4. WHEN a user adds scenario steps, THE Use Case Assistant SHALL validate that each step follows the subject-verb-object format
5. WHEN a user finishes the main scenario, THE Use Case Assistant SHALL prompt for extensions and alternative flows

### Requirement 2

**User Story:** As a user, I want the tool to validate my use case structure, so that I can ensure it follows Cockburn's best practices.

#### Acceptance Criteria

1. WHEN a use case title is provided, THE Use Case Assistant SHALL verify it expresses a goal rather than a function
2. WHEN scenario steps are entered, THE Use Case Assistant SHALL check that each step is written from the perspective of an actor or the system
3. WHEN a goal level is specified, THE Use Case Assistant SHALL validate it matches one of the standard levels (Summary, User Goal, Subfunction)
4. WHEN extensions are added, THE Use Case Assistant SHALL verify each extension references a valid step number from the main scenario
5. WHEN a use case is completed, THE Use Case Assistant SHALL validate that success guarantees are stated as conditions, not actions

### Requirement 3

**User Story:** As a user, I want to save and load use cases, so that I can work on them over multiple sessions.

#### Acceptance Criteria

1. WHEN a user completes a use case, THE Use Case Assistant SHALL persist the use case to a file in a structured format
2. WHEN a user requests to load a use case, THE Use Case Assistant SHALL read the file and restore all use case details
3. WHEN saving a use case, THE Use Case Assistant SHALL validate the file format is correct before writing
4. WHEN loading a use case, THE Use Case Assistant SHALL validate the file contents match the expected schema
5. WHEN a file operation fails, THE Use Case Assistant SHALL report a clear error message to the user

### Requirement 4

**User Story:** As a user, I want to list all my use cases, so that I can see what I've documented and select one to work on.

#### Acceptance Criteria

1. WHEN a user requests a list of use cases, THE Use Case Assistant SHALL display all saved use cases with their titles and primary actors
2. WHEN displaying the list, THE Use Case Assistant SHALL show the goal level for each use case
3. WHEN the list is empty, THE Use Case Assistant SHALL inform the user that no use cases exist
4. WHEN a user selects a use case from the list, THE Use Case Assistant SHALL load that use case for viewing or editing
5. WHEN listing use cases, THE Use Case Assistant SHALL sort them alphabetically by title

### Requirement 5

**User Story:** As a user, I want to export use cases to readable formats, so that I can share them with stakeholders.

#### Acceptance Criteria

1. WHEN a user requests export, THE Use Case Assistant SHALL generate a formatted text document containing the complete use case
2. WHEN exporting, THE Use Case Assistant SHALL include all sections: title, actors, goal level, design scope, trigger, preconditions, postconditions, success guarantees, main scenario, and extensions
3. WHEN formatting the export, THE Use Case Assistant SHALL use clear section headers and proper indentation
4. WHEN the export is complete, THE Use Case Assistant SHALL save the formatted document to a specified file path
5. WHEN an export operation fails, THE Use Case Assistant SHALL report the error without corrupting existing data

### Requirement 6

**User Story:** As a user, I want to edit existing use cases, so that I can refine and improve my documentation.

#### Acceptance Criteria

1. WHEN a user selects a use case to edit, THE Use Case Assistant SHALL display the current values for each field
2. WHEN editing a field, THE Use Case Assistant SHALL allow the user to modify the existing value or keep it unchanged
3. WHEN changes are made, THE Use Case Assistant SHALL validate the new values against the same rules as creation
4. WHEN editing is complete, THE Use Case Assistant SHALL save the updated use case to the same file location
5. WHEN a user cancels editing, THE Use Case Assistant SHALL discard changes and preserve the original use case

### Requirement 7

**User Story:** As a user, I want the CLI to provide helpful guidance, so that I can learn Cockburn's methodology while using the tool.

#### Acceptance Criteria

1. WHEN a user is prompted for input, THE Use Case Assistant SHALL display brief guidance on what is expected
2. WHEN a validation error occurs, THE Use Case Assistant SHALL explain why the input was rejected and provide an example of correct format
3. WHEN a user enters a help command, THE Use Case Assistant SHALL display information about available commands and their usage
4. WHEN starting a new use case, THE Use Case Assistant SHALL offer examples of well-formed use case titles
5. WHEN the user is defining goal levels, THE Use Case Assistant SHALL explain the difference between Summary, User Goal, and Subfunction levels

### Requirement 8

**User Story:** As a user, I want to delete use cases I no longer need, so that I can keep my workspace organized.

#### Acceptance Criteria

1. WHEN a user requests to delete a use case, THE Use Case Assistant SHALL prompt for confirmation before proceeding
2. WHEN deletion is confirmed, THE Use Case Assistant SHALL remove the use case file from storage
3. WHEN deletion is cancelled, THE Use Case Assistant SHALL preserve the use case without changes
4. WHEN a deletion operation fails, THE Use Case Assistant SHALL report the error and leave the file intact
5. WHEN a use case is successfully deleted, THE Use Case Assistant SHALL confirm the deletion to the user
