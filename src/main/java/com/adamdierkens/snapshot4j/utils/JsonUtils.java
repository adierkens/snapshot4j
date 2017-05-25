package com.adamdierkens.snapshot4j.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public final class JsonUtils {
    private JsonUtils() {}


    /**
     * To ensure that the diffs are accurate, the JsonObjects need their keys
     * to be sorted so they're printed in the same order.
     * This recurses through the JsonObjects and sorts their keys to ensure proper printing
     * @param ele - The JsonElement at the root of the tree to sort
     * @return a JsonElement with all keys sorted
     */
    public static JsonElement sort(JsonElement ele) {
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
}
