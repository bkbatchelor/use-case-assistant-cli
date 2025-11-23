package com.usecaseassistant.service;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;

/**
 * Property-based tests for validation consistency.
 * 
 * Feature: use-case-assistant, Property 17: Validation consistency
 * Validates: Requirements 6.3
 */
class ValidationConsistencyProperties {

    private final ValidationService validationService = new ValidationService();

    /**
     * Property: For any use case field, the validation rules applied during editing should be 
     * identical to those applied during creation. This is tested by validating the same input 
     * multiple times and ensuring consistent results.
     */
    @Property(tries = 100)
    @Label("Title validation should be consistent across multiple calls")
    void titleValidationIsConsistent(@ForAll String title) {
        ValidationResult result1 = validationService.validateTitle(title);
        ValidationResult result2 = validationService.validateTitle(title);
        
        Assertions.assertThat(result1.isValid())
            .as("Title validation should be consistent for '%s'", title)
            .isEqualTo(result2.isValid());
        
        Assertions.assertThat(result1.getErrors().size())
            .as("Title validation error count should be consistent for '%s'", title)
            .isEqualTo(result2.getErrors().size());
    }

    /**
     * Property: Goal level validation should be consistent.
     */
    @Property(tries = 100)
    @Label("Goal level validation should be consistent across multiple calls")
    void goalLevelValidationIsConsistent(@ForAll String goalLevel) {
        ValidationResult result1 = validationService.validateGoalLevel(goalLevel);
        ValidationResult result2 = validationService.validateGoalLevel(goalLevel);
        
        Assertions.assertThat(result1.isValid())
            .as("Goal level validation should be consistent for '%s'", goalLevel)
            .isEqualTo(result2.isValid());
        
        Assertions.assertThat(result1.getErrors().size())
            .as("Goal level validation error count should be consistent for '%s'", goalLevel)
            .isEqualTo(result2.getErrors().size());
    }

    /**
     * Property: Step validation should be consistent.
     */
    @Property(tries = 100)
    @Label("Step validation should be consistent across multiple calls")
    void stepValidationIsConsistent(@ForAll String step) {
        ValidationResult result1 = validationService.validateStep(step);
        ValidationResult result2 = validationService.validateStep(step);
        
        Assertions.assertThat(result1.isValid())
            .as("Step validation should be consistent for '%s'", step)
            .isEqualTo(result2.isValid());
        
        Assertions.assertThat(result1.getErrors().size())
            .as("Step validation error count should be consistent for '%s'", step)
            .isEqualTo(result2.getErrors().size());
    }

    /**
     * Property: Success guarantee validation should be consistent.
     */
    @Property(tries = 100)
    @Label("Success guarantee validation should be consistent across multiple calls")
    void successGuaranteeValidationIsConsistent(@ForAll String guarantee) {
        ValidationResult result1 = validationService.validateSuccessGuarantee(guarantee);
        ValidationResult result2 = validationService.validateSuccessGuarantee(guarantee);
        
        Assertions.assertThat(result1.isValid())
            .as("Success guarantee validation should be consistent for '%s'", guarantee)
            .isEqualTo(result2.isValid());
        
        Assertions.assertThat(result1.getErrors().size())
            .as("Success guarantee validation error count should be consistent for '%s'", guarantee)
            .isEqualTo(result2.getErrors().size());
    }

    /**
     * Property: Validation should be deterministic - same input always produces same output.
     */
    @Property(tries = 100)
    @Label("Validation should be deterministic across all validation methods")
    void validationIsDeterministic(
            @ForAll String title,
            @ForAll String goalLevel,
            @ForAll String step,
            @ForAll String guarantee) {
        
        // Run validation multiple times
        for (int i = 0; i < 3; i++) {
            ValidationResult titleResult = validationService.validateTitle(title);
            ValidationResult goalLevelResult = validationService.validateGoalLevel(goalLevel);
            ValidationResult stepResult = validationService.validateStep(step);
            ValidationResult guaranteeResult = validationService.validateSuccessGuarantee(guarantee);
            
            // Verify results are consistent
            Assertions.assertThat(titleResult.isValid())
                .as("Title validation should be deterministic on iteration %d", i)
                .isEqualTo(validationService.validateTitle(title).isValid());
            
            Assertions.assertThat(goalLevelResult.isValid())
                .as("Goal level validation should be deterministic on iteration %d", i)
                .isEqualTo(validationService.validateGoalLevel(goalLevel).isValid());
            
            Assertions.assertThat(stepResult.isValid())
                .as("Step validation should be deterministic on iteration %d", i)
                .isEqualTo(validationService.validateStep(step).isValid());
            
            Assertions.assertThat(guaranteeResult.isValid())
                .as("Success guarantee validation should be deterministic on iteration %d", i)
                .isEqualTo(validationService.validateSuccessGuarantee(guarantee).isValid());
        }
    }
}
