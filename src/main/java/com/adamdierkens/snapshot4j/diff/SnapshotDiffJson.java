package com.adamdierkens.snapshot4j.diff;

import com.google.gson.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by adierkens on 5/16/17.
 */
public class SnapshotDiffJson extends SnapshotDiff {

    private JsonElement expected;
    private JsonElement result;

    public SnapshotDiffJson(JsonElement expected, JsonElement result) {
        this.expected = sort(expected);
        this.result = sort(result);
    }

    /**
     * To ensure that the diffs are accurate, the JsonObjects need their keys
     * to be sorted so they're printed in the same order.
     * This recurses through the JsonObjects and sorts their keys to ensure proper printing
     * @param ele - The JsonElement at the root of the tree to sort
     * @return a JsonElement with all keys sorted
     */
    private JsonElement sort(JsonElement ele) {
        if (ele.isJsonObject()) {
            JsonObject object = ele.getAsJsonObject();
            JsonObject newJsonObj = new JsonObject();
            object.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(entry -> newJsonObj.add(entry.getKey(), sort(entry.getValue())));
            return newJsonObj;

        } else if (ele.isJsonArray()) {
            JsonArray array = ele.getAsJsonArray();
            for (int i=0; i<array.size(); i++) {
                array.set(i, sort(array.get(i)));
            }
            return array;
        }
        return ele;
    }

    @Override
    public String prettyPrintDiff() {
        Gson prettyPrintGson = new GsonBuilder().setPrettyPrinting().create();
        List<String> expectedLines = Arrays.asList(prettyPrintGson.toJson(this.expected).split("\n"));
        List<String> resultLines = Arrays.asList(prettyPrintGson.toJson(this.result).split("\n"));
        return prettyPrintDiff(expectedLines, resultLines);
    }
}
