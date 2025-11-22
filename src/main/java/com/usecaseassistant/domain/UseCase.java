package com.usecaseassistant.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a complete use case following Cockburn's methodology.
 * Immutable domain object.
 */
public final class UseCase {
    private final String id;
    private final String title;
    private final String primaryActor;
    private final GoalLevel goalLevel;
    private final String designScope;
    private final String trigger;
    private final List<String> preconditions;
    private final List<String> postconditions;
    private final List<String> successGuarantees;
    private final Scenario mainScenario;
    private final List<Extension> extensions;
    private final List<String> stakeholders;

    private UseCase(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id cannot be null");
        this.title = Objects.requireNonNull(builder.title, "title cannot be null");
        this.primaryActor = Objects.requireNonNull(builder.primaryActor, "primaryActor cannot be null");
        this.goalLevel = Objects.requireNonNull(builder.goalLevel, "goalLevel cannot be null");
        this.designScope = Objects.requireNonNull(builder.designScope, "designScope cannot be null");
        this.trigger = Objects.requireNonNull(builder.trigger, "trigger cannot be null");
        this.preconditions = Collections.unmodifiableList(new ArrayList<>(builder.preconditions));
        this.postconditions = Collections.unmodifiableList(new ArrayList<>(builder.postconditions));
        this.successGuarantees = Collections.unmodifiableList(new ArrayList<>(builder.successGuarantees));
        this.mainScenario = Objects.requireNonNull(builder.mainScenario, "mainScenario cannot be null");
        this.extensions = Collections.unmodifiableList(new ArrayList<>(builder.extensions));
        this.stakeholders = Collections.unmodifiableList(new ArrayList<>(builder.stakeholders));
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPrimaryActor() {
        return primaryActor;
    }

    public GoalLevel getGoalLevel() {
        return goalLevel;
    }

    public String getDesignScope() {
        return designScope;
    }

    public String getTrigger() {
        return trigger;
    }

    public List<String> getPreconditions() {
        return preconditions;
    }

    public List<String> getPostconditions() {
        return postconditions;
    }

    public List<String> getSuccessGuarantees() {
        return successGuarantees;
    }

    public Scenario getMainScenario() {
        return mainScenario;
    }

    public List<Extension> getExtensions() {
        return extensions;
    }

    public List<String> getStakeholders() {
        return stakeholders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UseCase useCase = (UseCase) o;
        return Objects.equals(id, useCase.id) &&
               Objects.equals(title, useCase.title) &&
               Objects.equals(primaryActor, useCase.primaryActor) &&
               goalLevel == useCase.goalLevel &&
               Objects.equals(designScope, useCase.designScope) &&
               Objects.equals(trigger, useCase.trigger) &&
               Objects.equals(preconditions, useCase.preconditions) &&
               Objects.equals(postconditions, useCase.postconditions) &&
               Objects.equals(successGuarantees, useCase.successGuarantees) &&
               Objects.equals(mainScenario, useCase.mainScenario) &&
               Objects.equals(extensions, useCase.extensions) &&
               Objects.equals(stakeholders, useCase.stakeholders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, primaryActor, goalLevel, designScope, trigger,
                          preconditions, postconditions, successGuarantees, mainScenario,
                          extensions, stakeholders);
    }

    @Override
    public String toString() {
        return "UseCase{" +
               "id='" + id + '\'' +
               ", title='" + title + '\'' +
               ", primaryActor='" + primaryActor + '\'' +
               ", goalLevel=" + goalLevel +
               '}';
    }

    public static final class Builder {
        private String id;
        private String title;
        private String primaryActor;
        private GoalLevel goalLevel;
        private String designScope;
        private String trigger;
        private List<String> preconditions = new ArrayList<>();
        private List<String> postconditions = new ArrayList<>();
        private List<String> successGuarantees = new ArrayList<>();
        private Scenario mainScenario;
        private List<Extension> extensions = new ArrayList<>();
        private List<String> stakeholders = new ArrayList<>();

        private Builder() {}

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder primaryActor(String primaryActor) {
            this.primaryActor = primaryActor;
            return this;
        }

        public Builder goalLevel(GoalLevel goalLevel) {
            this.goalLevel = goalLevel;
            return this;
        }

        public Builder designScope(String designScope) {
            this.designScope = designScope;
            return this;
        }

        public Builder trigger(String trigger) {
            this.trigger = trigger;
            return this;
        }

        public Builder preconditions(List<String> preconditions) {
            this.preconditions = new ArrayList<>(preconditions);
            return this;
        }

        public Builder addPrecondition(String precondition) {
            this.preconditions.add(precondition);
            return this;
        }

        public Builder postconditions(List<String> postconditions) {
            this.postconditions = new ArrayList<>(postconditions);
            return this;
        }

        public Builder addPostcondition(String postcondition) {
            this.postconditions.add(postcondition);
            return this;
        }

        public Builder successGuarantees(List<String> successGuarantees) {
            this.successGuarantees = new ArrayList<>(successGuarantees);
            return this;
        }

        public Builder addSuccessGuarantee(String successGuarantee) {
            this.successGuarantees.add(successGuarantee);
            return this;
        }

        public Builder mainScenario(Scenario mainScenario) {
            this.mainScenario = mainScenario;
            return this;
        }

        public Builder extensions(List<Extension> extensions) {
            this.extensions = new ArrayList<>(extensions);
            return this;
        }

        public Builder addExtension(Extension extension) {
            this.extensions.add(extension);
            return this;
        }

        public Builder stakeholders(List<String> stakeholders) {
            this.stakeholders = new ArrayList<>(stakeholders);
            return this;
        }

        public Builder addStakeholder(String stakeholder) {
            this.stakeholders.add(stakeholder);
            return this;
        }

        public UseCase build() {
            return new UseCase(this);
        }
    }
}
