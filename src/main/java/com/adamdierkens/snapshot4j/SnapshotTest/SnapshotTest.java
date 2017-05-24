package com.adamdierkens.snapshot4j.SnapshotTest;

import com.adamdierkens.snapshot4j.result.EmptyResult;
import com.adamdierkens.snapshot4j.result.ResultType;
import com.adamdierkens.snapshot4j.result.SnapshotResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import difflib.StringUtills;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SnapshotTest {
    private static final Logger LOG = LoggerFactory.getLogger(SnapshotTest.class);

    private File snapshotDir;

    public SnapshotTest() {
        snapshotDir = new File("resources");
    }

    public SnapshotTest(File saveDir) {
        this.snapshotDir = saveDir;
    }

    public static void takeSnapshot(File saveDir, String testName, JsonElement testInput) throws SnapshotTestException, IOException {
        SnapshotTest snapshotTest = new SnapshotTest(saveDir);
        snapshotTest.takeSnapshot(testName, testInput);
    }

    public static void takeSnapshot(File saveDir, String testName, String testInput) throws SnapshotTestException, IOException {
        SnapshotTest snapshotTest = new SnapshotTest(saveDir);
        snapshotTest.takeSnapshot(testName, testInput);
    }

    public void takeSnapshot(String testName, JsonElement testInput) throws SnapshotTestException, IOException {
        SnapshotResult actual = SnapshotResult.create(testInput);
        this.takeSnapshot(testName, actual);
    }

    public void takeSnapshot(String testName, String testInput) throws SnapshotTestException, IOException {
        SnapshotResult actual = SnapshotResult.create(testInput);
        this.takeSnapshot(testName, actual);
    }

    private void takeSnapshot(String testName, SnapshotResult actual) throws SnapshotTestException, IOException {
        SnapshotResult storedResult = readSnapshot(testName);
        String update = System.getProperty("updateSnapshot");

        if (update != null && update.length() != 0) {
            LOG.debug(String.format("Updating snapshot for test: %s", testName));
            this.writeSnapshot(testName, actual);
        } else if (storedResult.getType().equals(ResultType.Empty)) {
            LOG.debug(String.format("No existing snapshot for test: %s found. Storing result as baseline", testName));
            this.writeSnapshot(testName, actual);
        } else {
            // Compare the stored result against the actual one
            SnapshotTestException comparisonException = storedResult.compare(actual);
            if (comparisonException != null) {
                throw comparisonException;
            }
        }
    }

    private void writeSnapshot(String testname, SnapshotResult result) throws IOException {
        if (result.getType().equals(ResultType.Empty)) {
            return;
        }

        List<String> lines = Arrays.asList(result.getType().toString(), result.toString());
        Path file = Paths.get(snapshotDir.getAbsolutePath(), testname);
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    private String join(List<Object> strings, String seperator) {
        String prefix = "";
        StringBuilder sb = new StringBuilder();
        for (Object str : strings) {
            sb.append(prefix);
            sb.append(str);
            prefix = seperator;
        }
        return sb.toString();
    }

    SnapshotResult readSnapshot(String testName) throws IOException {
        List<Object> lines;
        Path file = Paths.get(snapshotDir.getAbsolutePath(), testName);

        try (Stream<String> stream = Files.lines(file)) {
            lines = stream.collect(Collectors.toList());
        } catch (NoSuchFileException noFile) {
            return EmptyResult.INSTANCE;
        }

        if (lines.size() == 1) {
            return EmptyResult.INSTANCE;
        }

        String type = (String) lines.get(0);
        String results = null;
        if (lines.size() > 1) {
            results = join(lines.subList(1, lines.size()), "\n");
        }

        if (type.equals(ResultType.Empty.toString()) || results == null) {
            return EmptyResult.INSTANCE;
        } else if (type.equals(ResultType.String.toString())) {
            return SnapshotResult.create(results);
        } else if (type.equals(ResultType.JSON.toString())){
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(results);
            return SnapshotResult.create(json);
        }

        LOG.warn(String.format("Unknown snapshot type: %s", type));
        return EmptyResult.INSTANCE;
    }
}
