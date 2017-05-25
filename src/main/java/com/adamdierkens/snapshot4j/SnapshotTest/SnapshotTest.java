package com.adamdierkens.snapshot4j.SnapshotTest;

import com.adamdierkens.snapshot4j.result.ResultType;
import com.adamdierkens.snapshot4j.result.SnapshotResult;
import com.adamdierkens.snapshot4j.result.SnapshotResultFactory;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class SnapshotTest {
    private static final Logger LOG = LoggerFactory.getLogger(SnapshotTest.class);

    private File snapshotDir;

    public SnapshotTest() {
        snapshotDir = new File("resources");
    }

    public SnapshotTest(File saveDir) {
        this.snapshotDir = saveDir;
    }

    public void takeSnapshot(String testName, JsonElement testInput) throws SnapshotTestException, IOException {
        SnapshotResult actual = SnapshotResultFactory.create(testInput);
        this.takeSnapshot(testName, actual);
    }

    public void takeSnapshot(String testName, String snapshotName, JsonElement testInput) throws SnapshotTestException, IOException {
        SnapshotResult actual = SnapshotResultFactory.create(testInput);
        this.takeSnapshot(testName, snapshotName, actual);
    }

    public void takeSnapshot(String testName, String testInput) throws SnapshotTestException, IOException {
        SnapshotResult actual = SnapshotResultFactory.create(testInput);
        this.takeSnapshot(testName, actual);
    }

    public void takeSnapshot(String testName, String snapshotName, String testInput) throws SnapshotTestException, IOException {
        SnapshotResult actual = SnapshotResultFactory.create(testInput);
        this.takeSnapshot(testName, snapshotName, actual);
    }

    private void takeSnapshot(String testName, SnapshotResult actual) throws SnapshotTestException, IOException {
        takeSnapshot(testName, SnapshotResultFactory.DEFAULT_SNAPSHOTNAME, actual);
    }

    private void takeSnapshot(String testName, String snapshotName, SnapshotResult actual) throws SnapshotTestException, IOException {
        SnapshotResult storedResult = SnapshotResultFactory.read(snapshotDir, testName, snapshotName);
        String update = System.getProperty("updateSnapshot");

        if (update != null && update.length() != 0) {
            LOG.debug(String.format("Updating snapshot for test: %s", testName));
            SnapshotResultFactory.append(snapshotDir, testName, snapshotName, actual);
        } else if (storedResult.getType().equals(ResultType.Empty)) {
            LOG.debug(String.format("No existing snapshot for test: %s found. Storing result as baseline", testName));
            SnapshotResultFactory.append(snapshotDir, testName, snapshotName, actual);
        } else {
            // Compare the stored result against the actual one
            SnapshotTestException comparisonException = storedResult.compare(actual);
            if (comparisonException != null) {
                throw comparisonException;
            }
        }
    }
}
