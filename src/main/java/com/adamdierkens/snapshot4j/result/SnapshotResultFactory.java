package com.adamdierkens.snapshot4j.result;

import com.google.gson.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SnapshotResultFactory {

    public static final String DEFAULT_SNAPSHOTNAME = "DEFAULT";
    private static final JsonParser JSON_PARSER = new JsonParser();
    private static final Gson PP_GSON = new GsonBuilder().setPrettyPrinting().create();

    private SnapshotResultFactory() {}

    /**
     * Creates an empty SnapshotResult
     * @return a SnapshotResult instance
     */
    public static SnapshotResult create() {
        return EmptyResult.INSTANCE;
    }

    /**
     * Creates a SnapshotResult from a JsonElement
     * @param element - The source JSON
     * @return a SnapshotResult instance
     */
    public static SnapshotResult create(JsonElement element) {
        return new JsonResult(element);
    }

    /**
     * Creates a SnapshotResult from a String
     * @param string - The source String
     * @return a SnapshotResult instance
     */
    public static SnapshotResult create(String string) {
        JsonElement je = new JsonPrimitive(string);
        return create(je);
    }

    private static String join(List<?> strings) {
        return join(strings, "");
    }

    private static String join(List<?> strings, String seperator) {
        String prefix = "";
        StringBuilder sb = new StringBuilder();
        for (Object str : strings) {
            sb.append(prefix);
            sb.append(str);
            prefix = seperator;
        }
        return sb.toString();
    }

    private static boolean isLegacy(List<String> lines) {
        if (lines.size() < 1) {
            return false;
        }
        String type = lines.get(0);
        try {
            ResultType.valueOf(type);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Fetch the current stored snapshot
     * @param snapshotDir - The directory where snapshots are stored
     * @param testName - The name of the test we're fetching
     * @param snapshotName - The name of the snapshot we're fetching (optional)
     * @return - A SnapshotResult of the stored snapshot
     */
    public static SnapshotResult read(File snapshotDir, String testName, String snapshotName) {

        JsonObject storedObj = getStoredSnapshot(snapshotDir, testName);

        if (snapshotName == null) {
            snapshotName = DEFAULT_SNAPSHOTNAME;
        }

        if (storedObj.has(snapshotName)) {
            return create(storedObj.get(snapshotName));
        }

        return create();
    }

    /**
     * Fetch the current stored snapshot
     * @param snapshotDir - The directory where snapshots are stored
     * @param testName - The name of the test we're fetching
     * @return - A SnapshotResult of the stored snapshot
     */
    public static SnapshotResult read(File snapshotDir, String testName) {
        return read(snapshotDir, testName, DEFAULT_SNAPSHOTNAME);
    }

    private static JsonObject getStoredSnapshot(File snapshotDir, String testName) {
        List<String> lines;
        Path file = Paths.get(snapshotDir.getAbsolutePath(), testName);
        try (Stream<String> stream = Files.lines(file)) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            return new JsonObject();
        }

        if (lines.size() == 0) {
            return new JsonObject();
        }

        boolean legacy = false;
        ResultType type = null;
        if (isLegacy(lines)) {
            legacy = true;
            type = ResultType.valueOf(lines.get(0));
            lines = lines.subList(1, lines.size());
        }

        JsonElement parsed;

        if (legacy && ResultType.String.equals(type)) {
            parsed = new JsonPrimitive(join(lines, "\n"));
        } else {
            parsed = JSON_PARSER.parse(join(lines));
        }

        if (parsed.isJsonObject()) {
            JsonObject storedObj = parsed.getAsJsonObject();
            if (legacy) {
                JsonObject rtnObj = new JsonObject();
                rtnObj.add(DEFAULT_SNAPSHOTNAME, storedObj);
                return rtnObj;
            }
            return storedObj;
        }
        return new JsonObject();
    }

    /**
     * Append a result to a snapshot on disk.
     * @param snapshotDir - The directory where snapshots are stored
     * @param testName - The name of the test we're storing
     * @param snapshotName - The name of the snapshot we're storing
     * @param result - The SnapshotResult to store
     * @throws IOException - When failing to read/write a file
     */
    public static void append(File snapshotDir, String testName, String snapshotName, SnapshotResult result)  throws IOException {
        JsonObject storedObj = getStoredSnapshot(snapshotDir, testName);

        JsonElement resultElement = result.toJson();

        if (resultElement == null) {
            // Nothing to do
            return;
        }

        storedObj.add(snapshotName, resultElement);
        Path file = Paths.get(snapshotDir.getAbsolutePath(), testName);
        List<String> lines = Arrays.asList(PP_GSON.toJson(storedObj));
        Files.write(file, lines, Charset.forName("UTF-8"));
    }
}
