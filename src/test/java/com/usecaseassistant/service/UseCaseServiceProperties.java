package com.usecaseassistant.service;

import com.usecaseassistant.domain.*;
import com.usecaseassistant.storage.Serializer;
import com.usecaseassistant.storage.UseCaseRepository;
import net.jqwik.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Property-based tests for UseCaseService.
 */
class UseCaseServiceProperties {

    /**
     * Property 9: List completeness and information
     * Feature: use-case-assistant, Property 9: List completeness and information
     * Validates: Requirements 4.1, 4.2
     * 
     * For any set of saved use cases, listing them should return all use cases 
     * with their title, primary actor, and goal level included in the display.
     */
    @Property(tries = 100)
    void listReturnsAllUseCasesWithCompleteInformation(@ForAll("useCaseLists") List<UseCase> useCases) throws IOException {
        // Setup: Create temporary storage
        Path tempDir = Files.createTempDirectory("usecase-test");
        try {
            UseCaseRepository repository = new UseCaseRepository(tempDir, new Serializer());
            ValidationService validationService = new ValidationService();
            UseCaseService service = new UseCaseService(repository, validationService);

            // Save all use cases
            for (UseCase useCase : useCases) {
                repository.save(useCase);
            }

            // List use cases
            List<UseCase> listed = service.listUseCases();

            // Verify all use cases are returned
            assert listed.size() == useCases.size() : 
                "List should return all saved use cases";

            // Verify each use case has complete information
            for (UseCase listedUseCase : listed) {
                assert listedUseCase.getTitle() != null && !listedUseCase.getTitle().isEmpty() :
                    "Listed use case should have a title";
                assert listedUseCase.getPrimaryActor() != null && !listedUseCase.getPrimaryActor().isEmpty() :
                    "Listed use case should have a primary actor";
                assert listedUseCase.getGoalLevel() != null :
                    "Listed use case should have a goal level";
            }

            // Verify all original use cases are present
            for (UseCase original : useCases) {
                boolean found = listed.stream()
                    .anyMatch(uc -> uc.getId().equals(original.getId()));
                assert found : "All saved use cases should be in the list";
            }
        } finally {
            // Cleanup
            deleteDirectory(tempDir);
        }
    }

    /**
     * Property 10: Alphabetical sorting
     * Feature: use-case-assistant, Property 10: Alphabetical sorting
     * Validates: Requirements 4.5
     * 
     * For any set of use cases with different titles, listing them should 
     * return them sorted alphabetically by title.
     */
    @Property(tries = 100)
    void listReturnsSortedByTitle(@ForAll("useCaseLists") List<UseCase> useCases) throws IOException {
        // Skip if list is too small to test sorting
        Assume.that(useCases.size() >= 2);

        // Setup: Create temporary storage
        Path tempDir = Files.createTempDirectory("usecase-test");
        try {
            UseCaseRepository repository = new UseCaseRepository(tempDir, new Serializer());
            ValidationService validationService = new ValidationService();
            UseCaseService service = new UseCaseService(repository, validationService);

            // Save all use cases
            for (UseCase useCase : useCases) {
                repository.save(useCase);
            }

            // List use cases
            List<UseCase> listed = service.listUseCases();

            // Verify alphabetical sorting
            List<String> titles = listed.stream()
                .map(UseCase::getTitle)
                .collect(Collectors.toList());

            List<String> sortedTitles = titles.stream()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());

            assert titles.equals(sortedTitles) :
                "Use cases should be sorted alphabetically by title (case-insensitive)";
        } finally {
            // Cleanup
            deleteDirectory(tempDir);
        }
    }

    /**
     * Property 11: List selection loads correct use case
     * Feature: use-case-assistant, Property 11: List selection loads correct use case
     * Validates: Requirements 4.4
     * 
     * For any use case in the list, selecting it should load the exact use case 
     * with all its original data intact.
     */
    @Property(tries = 100)
    void listSelectionLoadsCorrectUseCase(@ForAll("useCaseLists") List<UseCase> useCases) throws IOException {
        // Skip if list is empty
        Assume.that(!useCases.isEmpty());

        // Setup: Create temporary storage
        Path tempDir = Files.createTempDirectory("usecase-test");
        try {
            UseCaseRepository repository = new UseCaseRepository(tempDir, new Serializer());
            ValidationService validationService = new ValidationService();
            UseCaseService service = new UseCaseService(repository, validationService);

            // Save all use cases
            for (UseCase useCase : useCases) {
                repository.save(useCase);
            }

            // List use cases
            List<UseCase> listed = service.listUseCases();

            // For each use case in the list, load it and verify it matches
            for (UseCase listedUseCase : listed) {
                UseCase loaded = service.loadUseCase(listedUseCase.getId());
                
                assert loaded.equals(listedUseCase) :
                    "Loading a use case from the list should return the exact same use case";
            }
        } finally {
            // Cleanup
            deleteDirectory(tempDir);
        }
    }

    /**
     * Property 15: Edit persistence
     * Feature: use-case-assistant, Property 15: Edit persistence
     * Validates: Requirements 6.4
     * 
     * For any use case and valid modifications, completing the edit should save 
     * the updated use case to the same file location with all changes applied.
     */
    @Property(tries = 100)
    void editPersistsChanges(@ForAll("useCases") UseCase original, 
                            @ForAll("titles") String newTitle) throws IOException {
        // Setup: Create temporary storage
        Path tempDir = Files.createTempDirectory("usecase-test");
        try {
            UseCaseRepository repository = new UseCaseRepository(tempDir, new Serializer());
            ValidationService validationService = new ValidationService();
            UseCaseService service = new UseCaseService(repository, validationService);

            // Save original use case
            repository.save(original);

            // Create updated use case with new title
            UseCase updated = UseCase.builder()
                .id(original.getId())
                .title(newTitle)
                .primaryActor(original.getPrimaryActor())
                .goalLevel(original.getGoalLevel())
                .designScope(original.getDesignScope())
                .trigger(original.getTrigger())
                .preconditions(original.getPreconditions())
                .postconditions(original.getPostconditions())
                .successGuarantees(original.getSuccessGuarantees())
                .mainScenario(original.getMainScenario())
                .extensions(original.getExtensions())
                .stakeholders(original.getStakeholders())
                .build();

            // Update the use case
            service.updateUseCase(updated);

            // Load the use case and verify changes persisted
            UseCase loaded = service.loadUseCase(original.getId());
            
            assert loaded.getTitle().equals(newTitle) :
                "Updated title should be persisted";
            assert loaded.getId().equals(original.getId()) :
                "ID should remain the same after update";
        } finally {
            // Cleanup
            deleteDirectory(tempDir);
        }
    }

    /**
     * Property 16: Edit cancellation preservation
     * Feature: use-case-assistant, Property 16: Edit cancellation preservation
     * Validates: Requirements 6.5
     * 
     * For any use case being edited, cancelling the edit should leave the 
     * original use case unchanged in storage.
     */
    @Property(tries = 100)
    void editCancellationPreservesOriginal(@ForAll("useCases") UseCase original) throws IOException {
        // Setup: Create temporary storage
        Path tempDir = Files.createTempDirectory("usecase-test");
        try {
            UseCaseRepository repository = new UseCaseRepository(tempDir, new Serializer());
            ValidationService validationService = new ValidationService();
            UseCaseService service = new UseCaseService(repository, validationService);

            // Save original use case
            repository.save(original);

            // Simulate cancellation by not calling updateUseCase
            // (In real CLI, user would cancel before calling service)

            // Load the use case and verify it's unchanged
            UseCase loaded = service.loadUseCase(original.getId());
            
            assert loaded.equals(original) :
                "Use case should remain unchanged when edit is cancelled";
        } finally {
            // Cleanup
            deleteDirectory(tempDir);
        }
    }

    /**
     * Property 20: Deletion removes file
     * Feature: use-case-assistant, Property 20: Deletion removes file
     * Validates: Requirements 8.2
     * 
     * For any use case, confirming its deletion should remove the corresponding 
     * file from storage.
     */
    @Property(tries = 100)
    void deletionRemovesFile(@ForAll("useCases") UseCase useCase) throws IOException {
        // Setup: Create temporary storage
        Path tempDir = Files.createTempDirectory("usecase-test");
        try {
            UseCaseRepository repository = new UseCaseRepository(tempDir, new Serializer());
            ValidationService validationService = new ValidationService();
            UseCaseService service = new UseCaseService(repository, validationService);

            // Save use case
            repository.save(useCase);

            // Verify it exists
            assert service.exists(useCase.getId()) :
                "Use case should exist before deletion";

            // Delete the use case
            service.deleteUseCase(useCase.getId());

            // Verify it no longer exists
            assert !service.exists(useCase.getId()) :
                "Use case should not exist after deletion";
        } finally {
            // Cleanup
            deleteDirectory(tempDir);
        }
    }

    /**
     * Property 21: Deletion cancellation preservation
     * Feature: use-case-assistant, Property 21: Deletion cancellation preservation
     * Validates: Requirements 8.3
     * 
     * For any use case, cancelling its deletion should leave the file unchanged 
     * in storage.
     */
    @Property(tries = 100)
    void deletionCancellationPreservesFile(@ForAll("useCases") UseCase useCase) throws IOException {
        // Setup: Create temporary storage
        Path tempDir = Files.createTempDirectory("usecase-test");
        try {
            UseCaseRepository repository = new UseCaseRepository(tempDir, new Serializer());
            ValidationService validationService = new ValidationService();
            UseCaseService service = new UseCaseService(repository, validationService);

            // Save use case
            repository.save(useCase);

            // Simulate cancellation by not calling deleteUseCase
            // (In real CLI, user would cancel before calling service)

            // Verify it still exists
            assert service.exists(useCase.getId()) :
                "Use case should still exist when deletion is cancelled";

            // Verify data is intact
            UseCase loaded = service.loadUseCase(useCase.getId());
            assert loaded.equals(useCase) :
                "Use case data should be unchanged when deletion is cancelled";
        } finally {
            // Cleanup
            deleteDirectory(tempDir);
        }
    }

    // Providers for generating test data

    @Provide
    Arbitrary<List<UseCase>> useCaseLists() {
        return useCases().list().ofMinSize(0).ofMaxSize(10);
    }

    @Provide
    Arbitrary<UseCase> useCases() {
        return scenarios().flatMap(scenario -> 
            Builders.withBuilder(() -> UseCase.builder())
                .use(ids()).in((b, id) -> b.id(id))
                .use(titles()).in((b, title) -> b.title(title))
                .use(actors()).in((b, actor) -> b.primaryActor(actor))
                .use(goalLevels()).in((b, level) -> b.goalLevel(level))
                .use(designScopes()).in((b, scope) -> b.designScope(scope))
                .use(triggers()).in((b, trigger) -> b.trigger(trigger))
                .use(stringLists()).in((b, pre) -> b.preconditions(pre))
                .use(stringLists()).in((b, post) -> b.postconditions(post))
                .use(successGuarantees()).in((b, success) -> b.successGuarantees(success))
                .use(Arbitraries.just(scenario)).in((b, s) -> b.mainScenario(s))
                .use(validExtensionsFor(scenario)).in((b, ext) -> b.extensions(ext))
                .use(stringLists()).in((b, stake) -> b.stakeholders(stake))
                .build(UseCase.Builder::build)
        );
    }
    
    private Arbitrary<List<Extension>> validExtensionsFor(Scenario scenario) {
        int maxStep = scenario.getSteps().stream()
            .mapToInt(Step::getNumber)
            .max()
            .orElse(1);
        
        return Combinators.combine(
            Arbitraries.of(
                "Invalid credentials provided",
                "Network connection lost",
                "Validation fails"
            ),
            Arbitraries.integers().between(1, maxStep),
            steps().list().ofMinSize(1).ofMaxSize(5)
        ).as(Extension::of).list().ofMinSize(0).ofMaxSize(3);
    }

    @Provide
    Arbitrary<String> ids() {
        return Arbitraries.randomValue(random -> UUID.randomUUID().toString());
    }

    @Provide
    Arbitrary<String> titles() {
        return Arbitraries.of(
            "Purchase Items Online",
            "Register New User Account",
            "Process Payment Transaction",
            "Update User Profile",
            "Generate Monthly Report",
            "Submit Expense Claim",
            "Approve Leave Request",
            "Create Project Milestone"
        );
    }

    @Provide
    Arbitrary<String> actors() {
        return Arbitraries.of("User", "Customer", "Administrator", "Manager", "System");
    }

    @Provide
    Arbitrary<GoalLevel> goalLevels() {
        return Arbitraries.of(GoalLevel.class);
    }

    @Provide
    Arbitrary<String> designScopes() {
        return Arbitraries.strings()
            .alpha()
            .numeric()
            .withChars(' ', '.', ',')
            .ofMinLength(10)
            .ofMaxLength(200);
    }

    @Provide
    Arbitrary<String> triggers() {
        return Arbitraries.strings()
            .alpha()
            .numeric()
            .withChars(' ', '.', ',')
            .ofMinLength(5)
            .ofMaxLength(100);
    }

    @Provide
    Arbitrary<List<String>> stringLists() {
        return Arbitraries.strings()
            .alpha()
            .numeric()
            .withChars(' ', '.', ',')
            .ofMinLength(5)
            .ofMaxLength(100)
            .list()
            .ofMinSize(0)
            .ofMaxSize(5);
    }

    @Provide
    Arbitrary<List<String>> successGuarantees() {
        return Arbitraries.of(
            "User account is created and active",
            "Payment is recorded in the system",
            "Order is placed successfully",
            "Data is saved to the database"
        ).list().ofMinSize(1).ofMaxSize(3);
    }

    @Provide
    Arbitrary<Scenario> scenarios() {
        return steps().list().ofMinSize(1).ofMaxSize(10)
            .map(Scenario::of);
    }

    @Provide
    Arbitrary<Step> steps() {
        return Combinators.combine(
            Arbitraries.integers().between(1, 100),
            actors(),
            Arbitraries.of(
                "enters login credentials",
                "clicks submit button",
                "validates the input",
                "saves the data",
                "displays confirmation message"
            )
        ).as(Step::of);
    }

    // Helper method to delete directory recursively
    private void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // Ignore cleanup errors
                    }
                });
        }
    }
}
