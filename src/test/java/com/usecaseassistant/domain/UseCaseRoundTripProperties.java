package com.usecaseassistant.domain;

import com.usecaseassistant.storage.Serializer;
import net.jqwik.api.*;

import java.util.List;
import java.util.UUID;

/**
 * Property-based tests for use case domain model.
 * Feature: use-case-assistant, Property 1: Use case data round-trip preservation
 * Validates: Requirements 3.1, 3.2
 */
class UseCaseRoundTripProperties {

    private final Serializer serializer = new Serializer();

    /**
     * Property 1: Use case data round-trip preservation
     * For any valid use case, serializing it to JSON and then deserializing 
     * should produce an equivalent use case with all fields preserved.
     */
    @Property(tries = 100)
    void useCaseRoundTripPreservesAllData(@ForAll("useCases") UseCase original) {
        // Serialize to JSON
        String json = serializer.serialize(original);
        
        // Deserialize back to UseCase
        UseCase deserialized = serializer.deserialize(json);
        
        // Verify all fields are preserved
        assert original.equals(deserialized) : 
            "Round-trip serialization should preserve all use case data";
    }

    @Provide
    Arbitrary<UseCase> useCases() {
        // Combine in groups due to jqwik's 8-parameter limit
        return Combinators.combine(
            ids(),
            titles(),
            actors(),
            goalLevels(),
            designScopes(),
            triggers(),
            stringLists(),  // preconditions
            stringLists()   // postconditions
        ).as((id, title, actor, goalLevel, scope, trigger, pre, post) ->
            UseCase.builder()
                .id(id)
                .title(title)
                .primaryActor(actor)
                .goalLevel(goalLevel)
                .designScope(scope)
                .trigger(trigger)
                .preconditions(pre)
                .postconditions(post)
        ).flatMap(builder ->
            Combinators.combine(
                stringLists(),  // successGuarantees
                scenarios(),
                extensionLists(),
                stringLists()   // stakeholders
            ).as((success, scenario, ext, stake) ->
                builder
                    .successGuarantees(success)
                    .mainScenario(scenario)
                    .extensions(ext)
                    .stakeholders(stake)
                    .build()
            )
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
