package com.adamdierkens.snapshot4j.diff;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by adierkens on 5/17/17.
 */
public class SnapshotDiffJsonTest {
    @Test
    public void prettyPrintDiff() throws Exception {

        JsonParser parser = new JsonParser();
        JsonElement actual = parser.parse("{\"status\":\"success\",\"data\":{\"_Cached\":[]}}");
        JsonElement expected = parser.parse("{\"status\":\"success\",\"data\":{\"_Modified\":[\"constants\"],\"_Cached\":[]}}");

        SnapshotDiffJson snapshotDiffJson = new SnapshotDiffJson(expected, actual);

        Assert.assertEquals(snapshotDiffJson.prettyPrintDiff(), "\n" +
                " {\n" +
                "   \"data\": {\n" +
                "\u001B[31m-    \"_Cached\": [],\u001B[m\n" +
                "\u001B[31m-    \"_Modified\": [\u001B[m\n" +
                "\u001B[31m-      \"constants\"\u001B[m\n" +
                "\u001B[31m-    ]\u001B[m\n" +
                "\u001B[32m+    \"_Cached\": []\u001B[m\n" +
                "   },\n" +
                "   \"status\": \"success\"\n" +
                " }\n");

    }

}