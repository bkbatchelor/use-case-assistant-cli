package com.usecaseassistant.domain;

import com.usecaseassistant.storage.Serializer;
import net.jqwik.api.*;
import net.jqwik.api.Builders;

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
}
