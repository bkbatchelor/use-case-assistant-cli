package com.usecaseassistant.service;

import com.usecaseassistant.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service for validating use case components against Cockburn's methodology rules.
 */
@Service
public class ValidationService {

    private static final Pattern VERB_PATTERN = Pattern.compile("\\b(creates?|updates?|deletes?|adds?|removes?|sends?|receives?|validates?|checks?|confirms?|enters?|selects?|clicks?|submits?|saves?|loads?|displays?|shows?|navigates?|opens?|closes?|starts?|stops?|processes?|calculates?|generates?|retrieves?|searches?|filters?|sorts?|exports?|imports?)\\b", Pattern.CASE_INSENSITIVE);
    
    /**
     * Validates a use case title for goal-orientation.
     * A goal-oriented title expresses what the user wants to achieve, not a system function.
     * 
     * @param title the title to validate
     * @return validation result
     */
    public ValidationResult validateTitle(String title) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (title == null || title.trim().isEmpty()) {
            errors.add(new ValidationError("title", 
                "Title cannot be empty", 
                "Example: 'Purchase Items' or 'Register New User'"));
            return new ValidationResult(false, errors);
        }
        
        String trimmed = title.trim();
        
        // Check for function-oriented keywords that suggest implementation rather than goals
        String lowerTitle = trimmed.toLowerCase();
        if (lowerTitle.contains("manage") || lowerTitle.contains("maintain") || 
            lowerTitle.contains("administer") || lowerTitle.contains("handle")) {
            errors.add(new ValidationError("title",
                "Title appears to be function-oriented rather than goal-oriented. Focus on what the user wants to achieve.",
                "Example: Instead of 'Manage Users', use 'Add New User' or 'Update User Profile'"));
        }
        
        // Check if title is too vague
        if (trimmed.split("\\s+").length < 2) {
            errors.add(new ValidationError("title",
                "Title should be descriptive and express a clear goal",
                "Example: 'Purchase Items' or 'Register New User'"));
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates a goal level string.
     * 
     * @param level the goal level string to validate
     * @return validation result
     */
    public ValidationResult validateGoalLevel(String level) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (level == null || level.trim().isEmpty()) {
            errors.add(new ValidationError("goalLevel",
                "Goal level cannot be empty",
                "Valid values: SUMMARY, USER_GOAL, SUBFUNCTION"));
            return new ValidationResult(false, errors);
        }
        
        try {
            GoalLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            errors.add(new ValidationError("goalLevel",
                "Goal level must be one of: SUMMARY, USER_GOAL, or SUBFUNCTION",
                "Example: USER_GOAL"));
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates a scenario step for proper subject-verb-object format.
     * Steps should be written from the perspective of an actor or the system.
     * 
     * @param stepText the step text to validate (should include actor and action)
     * @return validation result
     */
    public ValidationResult validateStep(String stepText) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (stepText == null || stepText.trim().isEmpty()) {
            errors.add(new ValidationError("step",
                "Step cannot be empty",
                "Example: 'User enters login credentials' or 'System validates the input'"));
            return new ValidationResult(false, errors);
        }
        
        String trimmed = stepText.trim();
        
        // Check for minimum word count (actor + verb + object = at least 3 words)
        String[] words = trimmed.split("\\s+");
        if (words.length < 3) {
            errors.add(new ValidationError("step",
                "Step should follow subject-verb-object format with an actor and action",
                "Example: 'User enters login credentials' or 'System validates the input'"));
            return new ValidationResult(false, errors);
        }
        
        // Check for presence of a verb
        if (!VERB_PATTERN.matcher(trimmed).find()) {
            errors.add(new ValidationError("step",
                "Step should contain an action verb",
                "Example: 'User enters login credentials' or 'System validates the input'"));
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates an extension's branch point against the main scenario.
     * 
     * @param extension the extension to validate
     * @param mainScenario the main scenario to validate against
     * @return validation result
     */
    public ValidationResult validateExtension(Extension extension, Scenario mainScenario) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (extension == null) {
            errors.add(new ValidationError("extension",
                "Extension cannot be null",
                null));
            return new ValidationResult(false, errors);
        }
        
        if (mainScenario == null || mainScenario.getSteps().isEmpty()) {
            errors.add(new ValidationError("extension",
                "Cannot validate extension without a main scenario",
                null));
            return new ValidationResult(false, errors);
        }
        
        int branchPoint = extension.getBranchPoint();
        int maxStepNumber = mainScenario.getSteps().stream()
            .mapToInt(Step::getNumber)
            .max()
            .orElse(0);
        
        if (branchPoint < 1 || branchPoint > maxStepNumber) {
            errors.add(new ValidationError("extension",
                String.format("Extension branch point %d does not reference a valid step in the main scenario (1-%d)",
                    branchPoint, maxStepNumber),
                String.format("Example: Use a step number between 1 and %d", maxStepNumber)));
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates a success guarantee is stated as a condition, not an action.
     * Success guarantees should describe a state, not a process.
     * 
     * @param guarantee the success guarantee to validate
     * @return validation result
     */
    public ValidationResult validateSuccessGuarantee(String guarantee) {
        List<ValidationError> errors = new ArrayList<>();
        
        if (guarantee == null || guarantee.trim().isEmpty()) {
            errors.add(new ValidationError("successGuarantee",
                "Success guarantee cannot be empty",
                "Example: 'User account is created and active' or 'Payment is recorded in the system'"));
            return new ValidationResult(false, errors);
        }
        
        String trimmed = guarantee.trim();
        
        // Check for action-oriented language (imperative verbs at the start)
        String[] words = trimmed.split("\\s+");
        if (words.length > 0) {
            String firstWord = words[0].toLowerCase();
            // Common imperative verbs that indicate an action rather than a condition
            if (firstWord.matches("create|update|delete|add|remove|send|receive|validate|check|confirm|enter|select|click|submit|save|load|display|show|navigate|open|close|start|stop|process|calculate|generate|retrieve|search|filter|sort|export|import")) {
                errors.add(new ValidationError("successGuarantee",
                    "Success guarantee should be stated as a condition (describing a state), not an action",
                    "Example: Instead of 'Create user account', use 'User account is created and active'"));
            }
        }
        
        // Check for presence of "is", "are", "has", "have" which indicate state
        String lowerGuarantee = trimmed.toLowerCase();
        boolean hasStateIndicator = lowerGuarantee.contains(" is ") || 
                                    lowerGuarantee.contains(" are ") ||
                                    lowerGuarantee.contains(" has ") ||
                                    lowerGuarantee.contains(" have ") ||
                                    lowerGuarantee.contains(" remains ") ||
                                    lowerGuarantee.contains(" exists ");
        
        if (!hasStateIndicator) {
            errors.add(new ValidationError("successGuarantee",
                "Success guarantee should describe a state using words like 'is', 'are', 'has', 'have', 'remains', or 'exists'",
                "Example: 'User account is created and active' or 'Payment is recorded in the system'"));
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Validates a complete use case by orchestrating all validation rules.
     * 
     * @param useCase the use case to validate
     * @return validation result containing all errors found
     */
    public ValidationResult validateUseCase(UseCase useCase) {
        List<ValidationError> allErrors = new ArrayList<>();
        
        if (useCase == null) {
            allErrors.add(new ValidationError("useCase", "Use case cannot be null", null));
            return new ValidationResult(false, allErrors);
        }
        
        // Validate title
        ValidationResult titleResult = validateTitle(useCase.getTitle());
        allErrors.addAll(titleResult.getErrors());
        
        // Validate goal level (convert enum to string for validation)
        if (useCase.getGoalLevel() == null) {
            allErrors.add(new ValidationError("goalLevel", "Goal level cannot be null", 
                "Valid values: SUMMARY, USER_GOAL, SUBFUNCTION"));
        }
        
        // Validate main scenario steps
        if (useCase.getMainScenario() != null) {
            for (Step step : useCase.getMainScenario().getSteps()) {
                String stepText = step.getActor() + " " + step.getAction();
                ValidationResult stepResult = validateStep(stepText);
                allErrors.addAll(stepResult.getErrors());
            }
        }
        
        // Validate extensions
        for (Extension extension : useCase.getExtensions()) {
            ValidationResult extensionResult = validateExtension(extension, useCase.getMainScenario());
            allErrors.addAll(extensionResult.getErrors());
        }
        
        // Validate success guarantees
        for (String guarantee : useCase.getSuccessGuarantees()) {
            ValidationResult guaranteeResult = validateSuccessGuarantee(guarantee);
            allErrors.addAll(guaranteeResult.getErrors());
        }
        
        return new ValidationResult(allErrors.isEmpty(), allErrors);
    }
}
