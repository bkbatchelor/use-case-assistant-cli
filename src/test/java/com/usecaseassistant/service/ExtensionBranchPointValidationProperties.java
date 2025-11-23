package com.usecaseassistant.service;

import com.usecaseassistant.domain.Extension;
import com.usecaseassistant.domain.Scenario;
import com.usecaseassistant.domain.Step;
import net.jqwik.api.*;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.List;

/**
 * Property-based tests for extension branch point validation.
 * 
 * Feature: use-case-assistant, Property 5: Extension branch point validation
 * Validates: Requirements 2.4
 */
class ExtensionBranchPointValidationProperties {

    private final ValidationService validationService = new ValidationService();

    /**
     * Property: For any extension and main scenario, the validation should verify that the 
     * extension's branch point references an existing step number in the main scenario.
     */
    @Property(tries = 100)
    @Label("Extensions with valid branch points should pass validation")
    void validBranchPointsPassValidation(
            @ForAll("scenarios") Scenario scenario,
            @ForAll("validBranchPoints") int branchPoint) {
        
        // Ensure branch point is within valid range for this scenario
        int maxStep = scenario.getSteps().stream()
            .mapToInt(Step::getNumber)
            .max()
            .orElse(0);
        
        Assume.that(branchPoint >= 1 && branchPoint <= maxStep);
        
        Extension extension = Extension.of("Some condition", branchPoint, List.of());
        ValidationResult result = validationService.validateExtension(extension, scenario);
        
        Assertions.assertThat(result.isValid())
            .as("Extension with branch point %d should be valid for scenario with %d steps", 
                branchPoint, maxStep)
            .isTrue();
    }

    /**
     * Property: Extensions with branch points outside the valid range should fail validation.
     */
    @Property(tries = 100)
    @Label("Extensions with invalid branch points should fail validation")
    void invalidBranchPointsFailValidation(
            @ForAll("scenarios") Scenario scenario,
            @ForAll("invalidBranchPoints") int branchPoint) {
        
        int maxStep = scenario.getSteps().stream()
            .mapToInt(Step::getNumber)
            .max()
            .orElse(0);
        
        // Ensure branch point is outside valid range
        Assume.that(branchPoint < 1 || branchPoint > maxStep);
        
        Extension extension = Extension.of("Some condition", branchPoint, List.of());
        ValidationResult result = validationService.validateExtension(extension, scenario);
        
        Assertions.assertThat(result.isValid())
            .as("Extension with branch point %d should be invalid for scenario with %d steps", 
                branchPoint, maxStep)
            .isFalse();
    }

    /**
     * Property: Validation should fail when scenario is null or empty.
     */
    @Property(tries = 100)
    @Label("Extensions should fail validation when scenario is null or empty")
    void extensionsFailValidationWithNullOrEmptyScenario(@ForAll("validBranchPoints") int branchPoint) {
        Extension extension = Extension.of("Some condition", branchPoint, List.of());
        
        // Test with null scenario
        ValidationResult resultNull = validationService.validateExtension(extension, null);
        Assertions.assertThat(resultNull.isValid())
            .as("Extension should be invalid when scenario is null")
            .isFalse();
        
        // Test with empty scenario
        Scenario emptyScenario = Scenario.of(List.of());
        ValidationResult resultEmpty = validationService.validateExtension(extension, emptyScenario);
        Assertions.assertThat(resultEmpty.isValid())
            .as("Extension should be invalid when scenario is empty")
            .isFalse();
    }

    @Provide
    Arbitrary<Scenario> scenarios() {
        return Arbitraries.integers().between(1, 10).flatMap(numSteps -> {
            List<Step> steps = new ArrayList<>();
            for (int i = 1; i <= numSteps; i++) {
                steps.add(Step.of(i, "Actor", "performs action " + i));
            }
            return Arbitraries.just(Scenario.of(steps));
        });
    }

    @Provide
    Arbitrary<Integer> validBranchPoints() {
        return Arbitraries.integers().between(1, 10);
    }

    @Provide
    Arbitrary<Integer> invalidBranchPoints() {
        return Arbitraries.oneOf(
            Arbitraries.integers().between(-100, 0),
            Arbitraries.integers().between(11, 100)
        );
    }
}
