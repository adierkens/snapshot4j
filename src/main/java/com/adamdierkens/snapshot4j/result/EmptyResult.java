package com.adamdierkens.snapshot4j.result;

import com.adamdierkens.snapshot4j.SnapshotTest.SnapshotTestException;
import com.google.gson.JsonElement;

public class EmptyResult extends SnapshotResult {

    public static final EmptyResult INSTANCE = new EmptyResult();

    @Override
    public ResultType getType() {
        return ResultType.Empty;
    }

    @Override
    public SnapshotTestException compare(SnapshotResult other) {
        if (!other.getType().equals(getType())) {
            return new SnapshotTestException(getType(), other.getType());
        }
        return null;
    }

    @Override
    public JsonElement toJson() {
        return null;
    }
}
