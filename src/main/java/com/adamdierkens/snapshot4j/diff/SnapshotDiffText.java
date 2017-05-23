package com.adamdierkens.snapshot4j.diff;

import java.util.Arrays;
import java.util.List;

public class SnapshotDiffText extends SnapshotDiff {

    private String expected;
    private String result;

    public SnapshotDiffText(String expected, String result) {
        this.expected = expected;
        this.result = result;
    }

    @Override
    public String prettyPrintDiff() {
        List<String> expectedLines = Arrays.asList(this.expected.split("\n"));
        List<String> resultLines = Arrays.asList(this.result.split("\n"));
        return prettyPrintDiff(expectedLines, resultLines);
    }
}
