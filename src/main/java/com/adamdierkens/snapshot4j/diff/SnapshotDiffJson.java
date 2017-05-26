package com.adamdierkens.snapshot4j.diff;

import com.adamdierkens.snapshot4j.utils.JsonUtils;
import com.google.gson.JsonElement;

import java.util.Arrays;
import java.util.List;

public class SnapshotDiffJson extends SnapshotDiff {

    private JsonElement expected;
    private JsonElement result;

    public SnapshotDiffJson(JsonElement expected, JsonElement result) {
        this.expected = JsonUtils.sort(expected);
        this.result = JsonUtils.sort(result);
    }

    @Override
    public String prettyPrintDiff() {
        List<String> expectedLines = Arrays.asList(JsonUtils.PP_GSON.toJson(this.expected).split("\n"));
        List<String> resultLines = Arrays.asList(JsonUtils.PP_GSON.toJson(this.result).split("\n"));
        return prettyPrintDiff(expectedLines, resultLines);
    }
}
