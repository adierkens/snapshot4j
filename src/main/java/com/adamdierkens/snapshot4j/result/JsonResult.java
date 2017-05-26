package com.adamdierkens.snapshot4j.result;

import com.adamdierkens.snapshot4j.SnapshotTest.SnapshotTestException;
import com.adamdierkens.snapshot4j.diff.SnapshotDiffJson;
import com.adamdierkens.snapshot4j.utils.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

public class JsonResult extends SnapshotResult {

    private JsonElement jsonElement;

    public JsonResult() {
        jsonElement = JsonNull.INSTANCE;
    }

    public JsonResult(JsonElement element) {
        jsonElement = JsonUtils.sort(element);
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
        return JsonUtils.PP_GSON.toJson(jsonElement);
    }

    @Override
    public JsonElement toJson() {
        return jsonElement;
    }
}
