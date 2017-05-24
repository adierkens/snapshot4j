package com.adamdierkens.snapshot4j.result;

import com.adamdierkens.snapshot4j.SnapshotTest.SnapshotTestException;
import com.adamdierkens.snapshot4j.diff.SnapshotDiffJson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

public class JsonResult extends SnapshotResult {

    private static final Gson PP_GSON = new GsonBuilder().setPrettyPrinting().create();
    private JsonElement jsonElement;

    public JsonResult() {
        jsonElement = JsonNull.INSTANCE;
    }

    public JsonResult(JsonElement element) {
        jsonElement = element;
    }

    @Override
    public ResultType getType() {
        return ResultType.JSON;
    }

    @Override
    public SnapshotTestException compare(SnapshotResult other) {
        if (!other.getType().equals(getType())) {
            return new SnapshotTestException(getType(), other.getType());
        }

        JsonResult otherJson = (JsonResult)other;

        if (!jsonElement.equals(otherJson.jsonElement)) {
            return new SnapshotTestException(new SnapshotDiffJson(jsonElement, otherJson.jsonElement));
        }

        return null;
    }

    @Override
    public String toString() {
        return PP_GSON.toJson(jsonElement);
    }
}
