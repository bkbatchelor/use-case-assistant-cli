package com.usecaseassistant.service;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;

/**
 * Property-based tests for title goal-orientation validation.
 * 
 * Feature: use-case-assistant, Property 3: Title goal-orientation validation
 * Validates: Requirements 2.1
 */
class TitleGoalOrientationValidationProperties {

    private final ValidationService validationService = new ValidationService();

    /**
     * Property: For any use case title, the validation should correctly distinguish between 
     * goal-oriented titles (expressing what the user wants to achieve) and function-oriented 
     * titles (describing system features).
     */
    @Property(tries = 100)
    @Label("Goal-oriented titles should pass validation")
    void goalOrientedTitlesPassValidation(@ForAll("goalOrientedTitles") String title) {
        ValidationResult result = validationService.validateTitle(title);
        Assertions.assertThat(result.isValid())
            .as("Title '%s' should be valid (goal-oriented)", title)
            .isTrue();
    }

    /**
     * Property: Function-oriented titles should fail validation.
     */
    @Property(tries = 100)
    @Label("Function-oriented titles should fail validation")
    void functionOrientedTitlesFailValidation(@ForAll("functionOrientedTitles") String title) {
        ValidationResult result = validationService.validateTitle(title);
        Assertions.assertThat(result.isValid())
            .as("Title '%s' should be invalid (function-oriented)", title)
            .isFalse();
    }

    /**
     * Property: Empty or very short titles should fail validation.
     */
    @Property(tries = 100)
    @Label("Empty or single-word titles should fail validation")
    void emptyOrShortTitlesFailValidation(@ForAll("emptyOrShortTitles") String title) {
        ValidationResult result = validationService.validateTitle(title);
        Assertions.assertThat(result.isValid())
            .as("Title '%s' should be invalid (empty or too short)", title)
            .isFalse();
    }

    @Provide
    Arbitrary<String> goalOrientedTitles() {
        return Arbitraries.of(
            "Purchase Items",
            "Register New User",
            "Update User Profile",
            "Cancel Order",
            "Search Products",
            "Submit Feedback",
            "Request Refund",
            "Book Appointment",
            "Transfer Funds",
            "Generate Report",
            "Export Data",
            "Import Configuration",
            "Reset Password",
            "Verify Email Address",
            "Complete Checkout",
            "Add Item to Cart",
            "Remove Item from Wishlist",
            "Schedule Meeting",
            "Approve Request",
            "Reject Application"
        );
    }

    @Provide
    Arbitrary<String> functionOrientedTitles() {
        return Arbitraries.of(
            "Manage Users",
            "Maintain Database",
            "Administer System",
            "Handle Requests",
            "Manage Orders",
            "Maintain Inventory",
            "Administer Accounts",
            "Handle Payments"
        );
    }

    @Provide
    Arbitrary<String> emptyOrShortTitles() {
        return Arbitraries.of(
            "",
            "   ",
            "Login",
            "Search",
            "Update",
            "Delete",
            "Create"
        );
    }
}
