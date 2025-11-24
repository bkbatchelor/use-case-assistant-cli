package com.usecaseassistant.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when use case validation fails.
 */
public class ValidationException extends RuntimeException {
    private final List<ValidationError> errors;

    public ValidationException(String message, List<ValidationError> errors) {
        super(message);
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }

    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (!errors.isEmpty()) {
            sb.append(": ");
            for (ValidationError error : errors) {
                sb.append("\n  - ").append(error.getField()).append(": ").append(error.getMessage());
            }
        }
        return sb.toString();
    }
}
