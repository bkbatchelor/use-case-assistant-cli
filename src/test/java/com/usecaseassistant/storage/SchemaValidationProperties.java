package com.usecaseassistant.storage;

import com.usecaseassistant.domain.*;
import net.jqwik.api.*;

import java.util.List;
import java.util.UUID;

/**
 * Property-based tests for JSON schema validation.
 * Feature: use-case-assistant, Property 7: Schema validation on load
 * Validates: Requirements 3.4
 */
class SchemaValidationProperties {

    private final Serializer serializer = new Serializer();

    /**
     * Property 7: Schema validation on load
     * For any JSON string, attempting to deserialize it should either produce 
     * a valid use case or fail with a clear schema validation error.
     */
    @Property(tries = 100)
    void validUseCaseJsonPassesSchemaValidation(@ForAll("useCases") UseCase useCase) {
        // Serialize a valid use case
        String json = serializer.serialize(useCase);
        
        // Should deserialize without throwing an exception
        UseCase deserialized = serializer.deserialize(json);
        
        assert deserialized != null : "Valid JSON should deserialize successfully";
    }

    @Property(tries = 100)
    void invalidJsonFailsWithClearError(@ForAll("invalidJson") String invalidJson) {
        try {
            serializer.deserialize(invalidJson);
            assert false : "Invalid JSON should throw SerializationException";
        } catch (SerializationException e) {
            // Expected - verify error message is clear
            assert e.getMessage() != null && !e.getMessage().isEmpty() : 
                "Error message should be clear and non-empty";
            assert e.getMessage().contains("Schema validation failed") || 
                   e.getMessage().contains("Invalid JSON format") ||
                   e.getMessage().contains("Malformed JSON") :
                "Error message should indicate validation or format issue";
        } catch (IllegalArgumentException e) {
            // Also acceptable for null/empty strings
            assert e.getMessage().contains("cannot be null or empty") :
                "Error message should explain the issue";
        }
    }

    @Property(tries = 100)
    void missingRequiredFieldsFailsValidation(@ForAll("useCasesWithMissingFields") String jsonWithMissingFields) {
        try {
            serializer.deserialize(jsonWithMissingFields);
            assert false : "JSON with missing required fields should fail validation";
        } catch (SerializationException e) {
            assert e.getMessage().contains("Schema validation failed") :
                "Should fail with schema validation error";
        }
    }

    @Property(tries = 100)
    void invalidGoalLevelFailsValidation(@ForAll("useCasesWithInvalidGoalLevel") String jsonWithInvalidGoalLevel) {
        try {
            serializer.deserialize(jsonWithInvalidGoalLevel);
            assert false : "JSON with invalid goal level should fail validation";
        } catch (SerializationException e) {
            assert e.getMessage().contains("Schema validation failed") :
                "Should fail with schema validation error for invalid enum value";
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
    Arbitrary<String> invalidJson() {
        return Arbitraries.oneOf(
            // Completely invalid JSON
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50),
            // Empty object
            Arbitraries.just("{}"),
            // Malformed JSON
            Arbitraries.just("{invalid}"),
            Arbitraries.just("[1, 2, 3]"),
            // Missing closing brace
            Arbitraries.just("{\"id\": \"test\"")
        );
    }

    @Provide
    Arbitrary<String> useCasesWithMissingFields() {
        return Arbitraries.oneOf(
            // Missing id
            Arbitraries.just("{\"title\":\"Test\",\"primaryActor\":\"User\",\"goalLevel\":\"USER_GOAL\",\"designScope\":\"System\",\"trigger\":\"Event\",\"preconditions\":[],\"postconditions\":[],\"successGuarantees\":[],\"mainScenario\":{\"steps\":[]},\"extensions\":[],\"stakeholders\":[]}"),
            // Missing title
            Arbitraries.just("{\"id\":\"123\",\"primaryActor\":\"User\",\"goalLevel\":\"USER_GOAL\",\"designScope\":\"System\",\"trigger\":\"Event\",\"preconditions\":[],\"postconditions\":[],\"successGuarantees\":[],\"mainScenario\":{\"steps\":[]},\"extensions\":[],\"stakeholders\":[]}"),
            // Missing mainScenario
            Arbitraries.just("{\"id\":\"123\",\"title\":\"Test\",\"primaryActor\":\"User\",\"goalLevel\":\"USER_GOAL\",\"designScope\":\"System\",\"trigger\":\"Event\",\"preconditions\":[],\"postconditions\":[],\"successGuarantees\":[],\"extensions\":[],\"stakeholders\":[]}")
        );
    }

    @Provide
    Arbitrary<String> useCasesWithInvalidGoalLevel() {
        return Arbitraries.of(
            "{\"id\":\"123\",\"title\":\"Test\",\"primaryActor\":\"User\",\"goalLevel\":\"INVALID_LEVEL\",\"designScope\":\"System\",\"trigger\":\"Event\",\"preconditions\":[],\"postconditions\":[],\"successGuarantees\":[],\"mainScenario\":{\"steps\":[]},\"extensions\":[],\"stakeholders\":[]}",
            "{\"id\":\"123\",\"title\":\"Test\",\"primaryActor\":\"User\",\"goalLevel\":\"summary\",\"designScope\":\"System\",\"trigger\":\"Event\",\"preconditions\":[],\"postconditions\":[],\"successGuarantees\":[],\"mainScenario\":{\"steps\":[]},\"extensions\":[],\"stakeholders\":[]}",
            "{\"id\":\"123\",\"title\":\"Test\",\"primaryActor\":\"User\",\"goalLevel\":123,\"designScope\":\"System\",\"trigger\":\"Event\",\"preconditions\":[],\"postconditions\":[],\"successGuarantees\":[],\"mainScenario\":{\"steps\":[]},\"extensions\":[],\"stakeholders\":[]}"
        );
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
}
