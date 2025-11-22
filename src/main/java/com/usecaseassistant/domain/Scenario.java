package com.usecaseassistant.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a sequence of steps in a use case.
 * Immutable value object.
 */
public final class Scenario {
    private final List<Step> steps;

    private Scenario(List<Step> steps) {
        this.steps = Collections.unmodifiableList(new ArrayList<>(
            Objects.requireNonNull(steps, "steps cannot be null")
        ));
    }

    public static Scenario of(List<Step> steps) {
        return new Scenario(steps);
    }

    public List<Step> getSteps() {
        return steps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scenario scenario = (Scenario) o;
        return Objects.equals(steps, scenario.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(steps);
    }

    @Override
    public String toString() {
        return "Scenario{steps=" + steps.size() + "}";
    }
}
