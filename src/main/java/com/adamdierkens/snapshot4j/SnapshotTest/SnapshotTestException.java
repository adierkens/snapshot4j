package com.adamdierkens.snapshot4j.SnapshotTest;

import com.adamdierkens.snapshot4j.diff.SnapshotDiff;
import com.adamdierkens.snapshot4j.result.ResultType;

public class SnapshotTestException extends Exception {
    public SnapshotTestException() {}

    public SnapshotTestException(String msg) {
        super(msg);
    }

    public SnapshotTestException(Throwable cause) {
        super(cause);
    }

    public SnapshotTestException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SnapshotTestException(ResultType expected, ResultType actual) {
        this(String.format("Snapshot result types differ. Got %s but expected %s", actual, expected));
    }

    public SnapshotTestException(Number expected, Number actual, Number delta) {
        this(String.format("Got %s but expected %s with delta %s", expected, actual, delta));
    }

    public SnapshotTestException(SnapshotDiff diff) {
        this(diff.prettyPrintDiff());
    }
}
