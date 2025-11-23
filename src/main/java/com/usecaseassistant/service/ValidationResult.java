package com.usecaseassistant.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the result of a validation operation.
 * Immutable value object.
 */
public final class ValidationResult {
    private final boolean valid;
    private final List<ValidationError> errors;

    public ValidationResult(boolean valid, List<ValidationError> errors) {
        this.valid = valid;
        this.errors = Collections.unmodifiableList(new ArrayList<>(
            Objects.requireNonNull(errors, "errors cannot be null")
        ));
    }

    public boolean isValid() {
        return valid;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationResult that = (ValidationResult) o;
        return valid == that.valid && Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, errors);
    }

    @Override
    public String toString() {
        return "ValidationResult{valid=" + valid + ", errors=" + errors + "}";
    }
}
