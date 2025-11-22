package com.usecaseassistant.domain;

import java.util.Objects;

/**
 * Represents a single step in a scenario.
 * Immutable value object following subject-verb-object format.
 */
public final class Step {
    private final int number;
    private final String actor;
    private final String action;

    private Step(int number, String actor, String action) {
        this.number = number;
        this.actor = Objects.requireNonNull(actor, "actor cannot be null");
        this.action = Objects.requireNonNull(action, "action cannot be null");
    }

    public static Step of(int number, String actor, String action) {
        return new Step(number, actor, action);
    }

    public int getNumber() {
        return number;
    }

    public String getActor() {
        return actor;
    }

    public String getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Step step = (Step) o;
        return number == step.number &&
               Objects.equals(actor, step.actor) &&
               Objects.equals(action, step.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, actor, action);
    }

    @Override
    public String toString() {
        return number + ". " + actor + " " + action;
    }
}
