package com.usecaseassistant.storage;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.usecaseassistant.domain.*;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Handles serialization and deserialization of use cases to/from JSON.
 */
public class Serializer {
    private final Gson gson;

    public Serializer() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(UseCase.class, new UseCaseAdapter())
                .registerTypeAdapter(Scenario.class, new ScenarioAdapter())
                .registerTypeAdapter(Step.class, new StepAdapter())
                .registerTypeAdapter(Extension.class, new ExtensionAdapter())
                .create();
    }

    /**
     * Serializes a use case to JSON string.
     */
    public String serialize(UseCase useCase) {
        if (useCase == null) {
            throw new IllegalArgumentException("UseCase cannot be null");
        }
        return gson.toJson(useCase);
    }

    /**
     * Deserializes a JSON string to a use case.
     */
    public UseCase deserialize(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        return gson.fromJson(json, UseCase.class);
    }

    // Type adapters for immutable domain objects
    
    private static class UseCaseAdapter implements JsonSerializer<UseCase>, JsonDeserializer<UseCase> {
        @Override
        public JsonElement serialize(UseCase src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", src.getId());
            obj.addProperty("title", src.getTitle());
            obj.addProperty("primaryActor", src.getPrimaryActor());
            obj.addProperty("goalLevel", src.getGoalLevel().name());
            obj.addProperty("designScope", src.getDesignScope());
            obj.addProperty("trigger", src.getTrigger());
            obj.add("preconditions", context.serialize(src.getPreconditions()));
            obj.add("postconditions", context.serialize(src.getPostconditions()));
            obj.add("successGuarantees", context.serialize(src.getSuccessGuarantees()));
            obj.add("mainScenario", context.serialize(src.getMainScenario()));
            obj.add("extensions", context.serialize(src.getExtensions()));
            obj.add("stakeholders", context.serialize(src.getStakeholders()));
            return obj;
        }

        @Override
        public UseCase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            Type stringListType = new TypeToken<List<String>>(){}.getType();
            Type extensionListType = new TypeToken<List<Extension>>(){}.getType();
            
            return UseCase.builder()
                    .id(obj.get("id").getAsString())
                    .title(obj.get("title").getAsString())
                    .primaryActor(obj.get("primaryActor").getAsString())
                    .goalLevel(GoalLevel.valueOf(obj.get("goalLevel").getAsString()))
                    .designScope(obj.get("designScope").getAsString())
                    .trigger(obj.get("trigger").getAsString())
                    .preconditions(context.deserialize(obj.get("preconditions"), stringListType))
                    .postconditions(context.deserialize(obj.get("postconditions"), stringListType))
                    .successGuarantees(context.deserialize(obj.get("successGuarantees"), stringListType))
                    .mainScenario(context.deserialize(obj.get("mainScenario"), Scenario.class))
                    .extensions(context.deserialize(obj.get("extensions"), extensionListType))
                    .stakeholders(context.deserialize(obj.get("stakeholders"), stringListType))
                    .build();
        }
    }

    private static class ScenarioAdapter implements JsonSerializer<Scenario>, JsonDeserializer<Scenario> {
        @Override
        public JsonElement serialize(Scenario src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.add("steps", context.serialize(src.getSteps()));
            return obj;
        }

        @Override
        public Scenario deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            Type stepListType = new TypeToken<List<Step>>(){}.getType();
            List<Step> steps = context.deserialize(obj.get("steps"), stepListType);
            return Scenario.of(steps);
        }
    }

    private static class StepAdapter implements JsonSerializer<Step>, JsonDeserializer<Step> {
        @Override
        public JsonElement serialize(Step src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("number", src.getNumber());
            obj.addProperty("actor", src.getActor());
            obj.addProperty("action", src.getAction());
            return obj;
        }

        @Override
        public Step deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            return Step.of(
                obj.get("number").getAsInt(),
                obj.get("actor").getAsString(),
                obj.get("action").getAsString()
            );
        }
    }

    private static class ExtensionAdapter implements JsonSerializer<Extension>, JsonDeserializer<Extension> {
        @Override
        public JsonElement serialize(Extension src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("condition", src.getCondition());
            obj.addProperty("branchPoint", src.getBranchPoint());
            obj.add("steps", context.serialize(src.getSteps()));
            return obj;
        }

        @Override
        public Extension deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            Type stepListType = new TypeToken<List<Step>>(){}.getType();
            List<Step> steps = context.deserialize(obj.get("steps"), stepListType);
            return Extension.of(
                obj.get("condition").getAsString(),
                obj.get("branchPoint").getAsInt(),
                steps
            );
        }
    }
}
