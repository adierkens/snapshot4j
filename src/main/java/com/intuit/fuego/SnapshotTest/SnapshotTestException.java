package com.intuit.fuego.SnapshotTest;

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
}
