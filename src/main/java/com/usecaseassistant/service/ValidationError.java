package com.usecaseassistant.service;

import java.util.Objects;

/**
 * Represents a validation error with field, message, and optional example.
 * Immutable value object.
 */
public final class ValidationError {
    private final String field;
    private final String message;
    private final String example;

    public ValidationError(String field, String message, String example) {
        this.field = Objects.requireNonNull(field, "field cannot be null");
        this.message = Objects.requireNonNull(message, "message cannot be null");
        this.example = example; // Can be null
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public String getExample() {
        return example;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationError that = (ValidationError) o;
        return Objects.equals(field, that.field) &&
               Objects.equals(message, that.message) &&
               Objects.equals(example, that.example);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, message, example);
    }

    @Override
    public String toString() {
        return "ValidationError{field='" + field + "', message='" + message + 
               (example != null ? "', example='" + example : "") + "'}";
    }
}
