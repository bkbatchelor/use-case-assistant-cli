package com.usecaseassistant.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an alternative flow or error condition that branches from the main scenario.
 * Immutable value object.
 */
public final class Extension {
    private final String condition;
    private final int branchPoint;
    private final List<Step> steps;

    private Extension(String condition, int branchPoint, List<Step> steps) {
        this.condition = Objects.requireNonNull(condition, "condition cannot be null");
        this.branchPoint = branchPoint;
        this.steps = Collections.unmodifiableList(new ArrayList<>(
            Objects.requireNonNull(steps, "steps cannot be null")
        ));
    }

    public static Extension of(String condition, int branchPoint, List<Step> steps) {
        return new Extension(condition, branchPoint, steps);
    }

    public String getCondition() {
        return condition;
    }

    public int getBranchPoint() {
        return branchPoint;
    }

    public List<Step> getSteps() {
        return steps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Extension extension = (Extension) o;
        return branchPoint == extension.branchPoint &&
               Objects.equals(condition, extension.condition) &&
               Objects.equals(steps, extension.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, branchPoint, steps);
    }

    @Override
    public String toString() {
        return "Extension{condition='" + condition + "', branchPoint=" + branchPoint + 
               ", steps=" + steps.size() + "}";
    }
}
