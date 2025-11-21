---
inclusion: always
---

# Product Overview

The Use Case Assistant is a CLI application that guides users through creating, managing, and exporting use cases following Alistair Cockburn's methodology from "Writing Effective Use Cases". The tool enforces best practices for documenting system behavior through interactive prompts, validation, and structured storage.

## Core Purpose

Help users create well-structured use case documentation by:
- Guiding them through Cockburn's methodology interactively
- Validating inputs against best practices (goal-oriented titles, proper step format, valid extensions)
- Persisting use cases as JSON files for reuse across sessions
- Exporting formatted documentation for stakeholder review

## Key Concepts

**Use Case Structure**: Each use case includes title, primary actor, goal level (Summary/UserGoal/Subfunction), design scope, trigger, preconditions, postconditions, success guarantees, main scenario steps, and extensions.

**Validation Focus**: The application enforces Cockburn's rules—goal-oriented titles, subject-verb-object step format, valid extension branch points, and success guarantees stated as conditions not actions.

**File-Based Storage**: Use cases are stored as individual JSON files in `~/.usecase-assistant/use-cases/` for simplicity and human readability.

## Architecture Principles

- **Layered design**: CLI interface → Application services → Domain model → Storage/Export
- **Separation of concerns**: CLI logic is independent from domain logic for potential reuse
- **Immutable domain objects**: Use case modifications create new instances
- **Validation as first-class concern**: Centralized validation applied consistently across all operations

## User Experience Goals

- **Educational**: Teach Cockburn's methodology through helpful prompts and validation feedback
- **Forgiving**: Allow users to retry inputs without losing work; provide clear error messages with examples
- **Efficient**: Support both creation and editing workflows; enable quick listing and selection of existing use cases
- **Safe**: Confirm destructive operations; validate before writing; preserve data on errors

## Development Priorities

1. Correctness over features—validation must be accurate and consistent
2. Clear error messages—users should understand what went wrong and how to fix it
3. Data integrity—never corrupt or lose user's use case documentation
4. Testability—property-based tests verify universal correctness properties
