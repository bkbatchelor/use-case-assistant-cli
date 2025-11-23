package com.usecaseassistant.service;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;

/**
 * Property-based tests for step format validation.
 * 
 * Feature: use-case-assistant, Property 2: Step format validation
 * Validates: Requirements 1.4, 2.2
 */
class StepFormatValidationProperties {

    private final ValidationService validationService = new ValidationService();

    /**
     * Property: For any string representing a scenario step, the validation should correctly 
     * identify whether it follows subject-verb-object format with an actor perspective.
     */
    @Property(tries = 1000)
    @Label("Valid steps with actor-verb-object format should pass validation")
    void validStepsPassValidation(@ForAll("validSteps") String step) {
        ValidationResult result = validationService.validateStep(step);
        Assertions.assertThat(result.isValid())
            .as("Step '%s' should be valid but got errors: %s", step, result.getErrors())
            .isTrue();
    }

    /**
     * Property: Steps that are too short (less than 3 words) should fail validation.
     */
    @Property(tries = 100)
    @Label("Steps with fewer than 3 words should fail validation")
    void shortStepsFailValidation(@ForAll("shortSteps") String step) {
        ValidationResult result = validationService.validateStep(step);
        Assertions.assertThat(result.isValid())
            .as("Step '%s' should be invalid (too short)", step)
            .isFalse();
    }

    /**
     * Property: Steps without verbs should fail validation.
     */
    @Property(tries = 100)
    @Label("Steps without action verbs should fail validation")
    void stepsWithoutVerbsFailValidation(@ForAll("stepsWithoutVerbs") String step) {
        ValidationResult result = validationService.validateStep(step);
        Assertions.assertThat(result.isValid())
            .as("Step '%s' should be invalid (no verb)", step)
            .isFalse();
    }

    /**
     * Property: Empty or null steps should fail validation.
     */
    @Property(tries = 100)
    @Label("Empty or whitespace-only steps should fail validation")
    void emptyStepsFailValidation(@ForAll("emptyOrWhitespaceSteps") String step) {
        ValidationResult result = validationService.validateStep(step);
        Assertions.assertThat(result.isValid())
            .as("Step '%s' should be invalid (empty or whitespace)", step)
            .isFalse();
    }

    @Provide
    Arbitrary<String> validSteps() {
        Arbitrary<String> actors = Arbitraries.of(
            "User", "System", "Administrator", "Customer", "Manager", "Database"
        );
        
        Arbitrary<String> verbs = Arbitraries.of(
            "enters", "validates", "creates", "updates", "deletes", "sends", 
            "receives", "checks", "confirms", "selects", "clicks", "submits",
            "saves", "loads", "displays", "shows", "navigates", "processes"
        );
        
        Arbitrary<String> objects = Arbitraries.of(
            "login credentials", "the input", "a new account", "user profile",
            "the order", "confirmation email", "payment information", "the form",
            "search results", "the document", "error message", "the data"
        );
        
        return Combinators.combine(actors, verbs, objects)
            .as((actor, verb, object) -> actor + " " + verb + " " + object);
    }

    @Provide
    Arbitrary<String> shortSteps() {
        return Arbitraries.of(
            "User enters",
            "System",
            "validates",
            "User clicks button"  // This has 3 words but might still be considered too short
        ).filter(s -> s.split("\\s+").length < 3);
    }

    @Provide
    Arbitrary<String> stepsWithoutVerbs() {
        return Arbitraries.of(
            "User the credentials",
            "System the input data",
            "Administrator new account information",
            "Customer payment details screen"
        );
    }

    @Provide
    Arbitrary<String> emptyOrWhitespaceSteps() {
        return Arbitraries.of(
            "",
            "   ",
            "\t",
            "\n",
            "  \t  \n  "
        );
    }
}
