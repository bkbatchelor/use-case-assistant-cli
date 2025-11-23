package com.usecaseassistant.service;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;

/**
 * Property-based tests for success guarantee format validation.
 * 
 * Feature: use-case-assistant, Property 6: Success guarantee format validation
 * Validates: Requirements 2.5
 */
class SuccessGuaranteeFormatValidationProperties {

    private final ValidationService validationService = new ValidationService();

    /**
     * Property: For any success guarantee string, the validation should verify it is stated 
     * as a condition (describing a state) rather than an action (describing a process).
     */
    @Property(tries = 100)
    @Label("Success guarantees stated as conditions should pass validation")
    void conditionBasedGuaranteesPassValidation(@ForAll("conditionBasedGuarantees") String guarantee) {
        ValidationResult result = validationService.validateSuccessGuarantee(guarantee);
        Assertions.assertThat(result.isValid())
            .as("Success guarantee '%s' should be valid (stated as condition)", guarantee)
            .isTrue();
    }

    /**
     * Property: Success guarantees stated as actions should fail validation.
     */
    @Property(tries = 100)
    @Label("Success guarantees stated as actions should fail validation")
    void actionBasedGuaranteesFailValidation(@ForAll("actionBasedGuarantees") String guarantee) {
        ValidationResult result = validationService.validateSuccessGuarantee(guarantee);
        Assertions.assertThat(result.isValid())
            .as("Success guarantee '%s' should be invalid (stated as action)", guarantee)
            .isFalse();
    }

    /**
     * Property: Empty success guarantees should fail validation.
     */
    @Property(tries = 100)
    @Label("Empty or whitespace success guarantees should fail validation")
    void emptyGuaranteesFailValidation(@ForAll("emptyGuarantees") String guarantee) {
        ValidationResult result = validationService.validateSuccessGuarantee(guarantee);
        Assertions.assertThat(result.isValid())
            .as("Success guarantee '%s' should be invalid (empty or whitespace)", guarantee)
            .isFalse();
    }

    @Provide
    Arbitrary<String> conditionBasedGuarantees() {
        return Arbitraries.of(
            "User account is created and active",
            "Payment is recorded in the system",
            "Order is confirmed and saved",
            "Data is persisted to the database",
            "User session is established",
            "Transaction is completed successfully",
            "Email notification has been sent",
            "Profile information is updated",
            "File is uploaded and stored",
            "Reservation is confirmed",
            "Inventory levels are adjusted",
            "Report is generated and available",
            "Settings are saved",
            "Authentication token is valid",
            "User preferences are stored",
            "Backup is created",
            "Log entry exists in the system",
            "Status is changed to approved",
            "Record remains in the database",
            "Connection is established"
        );
    }

    @Provide
    Arbitrary<String> actionBasedGuarantees() {
        return Arbitraries.of(
            "Create user account",
            "Record the payment",
            "Confirm the order",
            "Save the data",
            "Establish user session",
            "Complete the transaction",
            "Send email notification",
            "Update profile information",
            "Upload and store the file",
            "Confirm the reservation",
            "Adjust inventory levels",
            "Generate the report",
            "Save the settings",
            "Validate authentication token",
            "Store user preferences",
            "Create a backup",
            "Add log entry",
            "Change status to approved",
            "Delete the record",
            "Open the connection"
        );
    }

    @Provide
    Arbitrary<String> emptyGuarantees() {
        return Arbitraries.of(
            "",
            "   ",
            "\t",
            "\n",
            "  \t  \n  "
        );
    }
}
