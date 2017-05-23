package com.adamdierkens.snapshot4j.SnapshotTest;

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
    private static final Gson GSON = new GsonBuilder().create();

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
        SnapshotTestResult actual = new SnapshotTestResult(testInput);
        this.takeSnapshot(testName, actual);
    }

    public void takeSnapshot(String testName, String testInput) throws SnapshotTestException, IOException {
        SnapshotTestResult actual = new SnapshotTestResult(testInput);
        this.takeSnapshot(testName, actual);
    }

    private void takeSnapshot(String testName, SnapshotTestResult actual) throws SnapshotTestException, IOException {
        SnapshotTestResult storedResult = readSnapshot(testName);
        String update = System.getProperty("updateSnapshot");

        if (update != null && update.length() != 0) {
            LOG.debug(String.format("Updating snapshot for test: %s", testName));
            this.writeSnapshot(testName, actual);
        } else if (storedResult.getResultType().equals(SnapshotTestResult.SnapshotTestResultType.Empty)) {
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

    private void writeSnapshot(String testname, SnapshotTestResult result) throws IOException {
        if (result.getResultType().equals(SnapshotTestResult.SnapshotTestResultType.Empty)) {
            return;
        }

        List<String> lines = Arrays.asList(result.getResultType().toString(), result.toString());
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

    private SnapshotTestResult readSnapshot(String testName) throws IOException {
        List<Object> lines;
        Path file = Paths.get(snapshotDir.getAbsolutePath(), testName);

        try (Stream<String> stream = Files.lines(file)) {
            lines = stream.collect(Collectors.toList());
        } catch (NoSuchFileException noFile) {
            return new SnapshotTestResult();
        }

        if (lines.size() == 1) {
            return new SnapshotTestResult();
        }

        String type = (String) lines.get(0);
        String results = null;
        if (lines.size() > 1) {
            results = join(lines.subList(1, lines.size()), "\n");
        }

        if (type.equals(SnapshotTestResult.SnapshotTestResultType.Empty.toString()) || results == null) {
            return new SnapshotTestResult();
        } else if (type.equals(SnapshotTestResult.SnapshotTestResultType.String.toString())) {
            return new SnapshotTestResult(results);
        } else {
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(results);
            return new SnapshotTestResult(json);
        }
    }
}
