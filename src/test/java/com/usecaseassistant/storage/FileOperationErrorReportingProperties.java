package com.usecaseassistant.storage;

import com.usecaseassistant.domain.*;
import net.jqwik.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Property-based tests for file operation error reporting.
 * Feature: use-case-assistant, Property 8: File operation error reporting
 * Validates: Requirements 3.5, 5.5, 8.4
 */
class FileOperationErrorReportingProperties {

    /**
     * Property 8: File operation error reporting
     * For any file operation that fails (save, load, delete), the system should 
     * generate an error message that clearly describes what went wrong.
     */
    @Property(tries = 100)
    void saveWithInvalidSerializerProducesDescriptiveError(@ForAll("useCases") UseCase useCase) {
        Path tempDir = createTempDirectory();
        try {
            // Create a serializer that will fail
            Serializer brokenSerializer = new Serializer() {
                @Override
                public String serialize(UseCase uc) {
                    throw new SerializationException("Intentional serialization failure for testing");
                }
            };
            UseCaseRepository repository = new UseCaseRepository(tempDir, brokenSerializer);

            try {
                repository.save(useCase);
                assert false : "Expected StorageException to be thrown";
            } catch (StorageException e) {
                // Verify error message is descriptive
                String message = e.getMessage();
                assert message != null && !message.isEmpty() : 
                    "Error message should not be null or empty";
                assert message.contains("Failed") || message.contains("failed") || 
                       message.contains("serialize") : 
                    "Error message should indicate serialization failure";
            }
        } finally {
            cleanupDirectory(tempDir);
        }
    }

    @Property(tries = 100)
    void loadNonExistentUseCaseProducesDescriptiveError(@ForAll("ids") String id) {
        Path tempDir = createTempDirectory();
        try {
            Serializer serializer = new Serializer();
            UseCaseRepository repository = new UseCaseRepository(tempDir, serializer);

            try {
                repository.load(id);
                assert false : "Expected StorageException for non-existent use case";
            } catch (StorageException e) {
                // Verify error message is descriptive
                String message = e.getMessage();
                assert message != null && !message.isEmpty() : 
                    "Error message should not be null or empty";
                assert message.contains("not found") || message.contains("Use case") : 
                    "Error message should indicate use case was not found";
            }
        } finally {
            cleanupDirectory(tempDir);
        }
    }

    @Property(tries = 100)
    void deleteNonExistentUseCaseProducesDescriptiveError(@ForAll("ids") String id) {
        Path tempDir = createTempDirectory();
        try {
            Serializer serializer = new Serializer();
            UseCaseRepository repository = new UseCaseRepository(tempDir, serializer);

            try {
                repository.delete(id);
                assert false : "Expected StorageException for non-existent use case";
            } catch (StorageException e) {
                // Verify error message is descriptive
                String message = e.getMessage();
                assert message != null && !message.isEmpty() : 
                    "Error message should not be null or empty";
                assert message.contains("not found") || message.contains("Use case") : 
                    "Error message should indicate use case was not found";
            }
        } finally {
            cleanupDirectory(tempDir);
        }
    }

    @Property(tries = 100)
    void loadMalformedJsonProducesDescriptiveError(@ForAll("ids") String id) {
        Path tempDir = createTempDirectory();
        try {
            Serializer serializer = new Serializer();
            UseCaseRepository repository = new UseCaseRepository(tempDir, serializer);

            // Write malformed JSON to file
            Path filePath = tempDir.resolve(id + ".json");
            Files.writeString(filePath, "{ invalid json }");

            try {
                repository.load(id);
                assert false : "Expected StorageException for malformed JSON";
            } catch (StorageException e) {
                // Verify error message is descriptive
                String message = e.getMessage();
                assert message != null && !message.isEmpty() : 
                    "Error message should not be null or empty";
                assert message.contains("Failed") || message.contains("deserialize") || 
                       message.contains("Invalid") || message.contains("Schema") : 
                    "Error message should indicate deserialization or validation failure";
            }
        } catch (IOException e) {
            throw new RuntimeException("Test setup failed", e);
        } finally {
            cleanupDirectory(tempDir);
        }
    }

    // Generators

    @Provide
    Arbitrary<UseCase> useCases() {
        return Builders.withBuilder(() -> UseCase.builder())
            .use(ids()).in((b, id) -> b.id(id))
            .use(titles()).in((b, title) -> b.title(title))
            .use(actors()).in((b, actor) -> b.primaryActor(actor))
            .use(goalLevels()).in((b, level) -> b.goalLevel(level))
            .use(designScopes()).in((b, scope) -> b.designScope(scope))
            .use(triggers()).in((b, trigger) -> b.trigger(trigger))
            .use(stringLists()).in((b, pre) -> b.preconditions(pre))
            .use(stringLists()).in((b, post) -> b.postconditions(post))
            .use(stringLists()).in((b, success) -> b.successGuarantees(success))
            .use(scenarios()).in((b, scenario) -> b.mainScenario(scenario))
            .use(extensionLists()).in((b, ext) -> b.extensions(ext))
            .use(stringLists()).in((b, stake) -> b.stakeholders(stake))
            .build(UseCase.Builder::build);
    }

    @Provide
    Arbitrary<String> ids() {
        return Arbitraries.strings()
            .alpha()
            .numeric()
            .withChars('-')
            .ofMinLength(10)
            .ofMaxLength(50)
            .map(s -> UUID.randomUUID().toString());
    }

    @Provide
    Arbitrary<String> titles() {
        return Arbitraries.strings()
            .alpha()
            .numeric()
            .withChars(' ', '-', '\'')
            .ofMinLength(5)
            .ofMaxLength(100);
    }

    @Provide
    Arbitrary<String> actors() {
        return Arbitraries.strings()
            .alpha()
            .withChars(' ')
            .ofMinLength(3)
            .ofMaxLength(50);
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
            .ofMaxSize(10);
    }

    @Provide
    Arbitrary<Scenario> scenarios() {
        return steps().list().ofMinSize(1).ofMaxSize(20)
            .map(Scenario::of);
    }

    @Provide
    Arbitrary<Step> steps() {
        return Combinators.combine(
            Arbitraries.integers().between(1, 100),
            actors(),
            Arbitraries.strings()
                .alpha()
                .numeric()
                .withChars(' ', '.', ',')
                .ofMinLength(5)
                .ofMaxLength(100)
        ).as(Step::of);
    }

    @Provide
    Arbitrary<List<Extension>> extensionLists() {
        return extensions().list().ofMinSize(0).ofMaxSize(5);
    }

    @Provide
    Arbitrary<Extension> extensions() {
        return Combinators.combine(
            Arbitraries.strings()
                .alpha()
                .numeric()
                .withChars(' ', '.', ',')
                .ofMinLength(5)
                .ofMaxLength(100),
            Arbitraries.integers().between(1, 20),
            steps().list().ofMinSize(1).ofMaxSize(10)
        ).as(Extension::of);
    }

    // Helper methods

    private Path createTempDirectory() {
        try {
            return Files.createTempDirectory("usecase-test-");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp directory", e);
        }
    }

    private void cleanupDirectory(Path directory) {
        try {
            if (Files.exists(directory)) {
                Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ignored) {
                            // Best effort cleanup
                        }
                    });
            }
        } catch (IOException ignored) {
            // Best effort cleanup
        }
    }
}
