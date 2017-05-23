package com.adamdierkens.snapshot4j.result;

import com.adamdierkens.snapshot4j.SnapshotTest.SnapshotTestException;
import com.adamdierkens.snapshot4j.diff.SnapshotDiffText;

public class StringResult extends SnapshotResult {

    private String result;

    public StringResult() {
        result = "";
    }

    public StringResult(String s) {
        result = s;
    }

    @Override
    public ResultType getType() {
        return ResultType.String;
    }

    @Override
    public SnapshotTestException compare(SnapshotResult other) {
        if (!other.getType().equals(getType())) {
            return new SnapshotTestException(getType(), other.getType());
        }

        StringResult stringResult = (StringResult) other;

        if (!result.equals(stringResult.result)) {
            return new SnapshotTestException(new SnapshotDiffText(result, stringResult.result));
        }

        return null;
    }

    @Override
    public String toString() {
        return result;
    }
}
