package com.usecaseassistant.service;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;

/**
 * Property-based tests for goal level enumeration validation.
 * 
 * Feature: use-case-assistant, Property 4: Goal level enumeration validation
 * Validates: Requirements 2.3
 */
class GoalLevelEnumerationValidationProperties {

    private final ValidationService validationService = new ValidationService();

    /**
     * Property: For any goal level string, the validation should accept only "Summary", 
     * "UserGoal", or "Subfunction" and reject all other values.
     */
    @Property(tries = 100)
    @Label("Valid goal level strings should pass validation")
    void validGoalLevelsPassValidation(@ForAll("validGoalLevels") String goalLevel) {
        ValidationResult result = validationService.validateGoalLevel(goalLevel);
        Assertions.assertThat(result.isValid())
            .as("Goal level '%s' should be valid", goalLevel)
            .isTrue();
    }

    /**
     * Property: Invalid goal level strings should fail validation.
     */
    @Property(tries = 100)
    @Label("Invalid goal level strings should fail validation")
    void invalidGoalLevelsFailValidation(@ForAll("invalidGoalLevels") String goalLevel) {
        ValidationResult result = validationService.validateGoalLevel(goalLevel);
        Assertions.assertThat(result.isValid())
            .as("Goal level '%s' should be invalid", goalLevel)
            .isFalse();
    }

    /**
     * Property: Empty or null goal levels should fail validation.
     */
    @Property(tries = 100)
    @Label("Empty or whitespace goal levels should fail validation")
    void emptyGoalLevelsFailValidation(@ForAll("emptyGoalLevels") String goalLevel) {
        ValidationResult result = validationService.validateGoalLevel(goalLevel);
        Assertions.assertThat(result.isValid())
            .as("Goal level '%s' should be invalid (empty or whitespace)", goalLevel)
            .isFalse();
    }

    @Provide
    Arbitrary<String> validGoalLevels() {
        return Arbitraries.of(
            "SUMMARY",
            "USER_GOAL",
            "SUBFUNCTION",
            "summary",
            "user_goal",
            "subfunction",
            "Summary",
            "User_Goal",
            "Subfunction"
        );
    }

    @Provide
    Arbitrary<String> invalidGoalLevels() {
        return Arbitraries.of(
            "INVALID",
            "HIGH",
            "LOW",
            "MEDIUM",
            "BUSINESS",
            "SYSTEM",
            "USER",
            "GOAL",
            "FUNCTION",
            "SUB",
            "SUMMARY_LEVEL",
            "USER_GOAL_LEVEL",
            "SUBFUNCTION_LEVEL",
            "SummaryGoal",
            "UserGoalLevel",
            "SubfunctionLevel",
            "random",
            "test",
            "123",
            "SUMMARY USER_GOAL"
        );
    }

    @Provide
    Arbitrary<String> emptyGoalLevels() {
        return Arbitraries.of(
            "",
            "   ",
            "\t",
            "\n",
            "  \t  \n  "
        );
    }
}
