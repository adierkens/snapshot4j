package com.adamdierkens.snapshot4j.result;

import com.adamdierkens.snapshot4j.SnapshotTest.SnapshotTestException;
import com.google.gson.JsonElement;

public abstract class SnapshotResult {


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
        return new StringResult(string);
    }

    /**
     * Gets the type of the stored result
     * @return - ResultType - The type of the stored result
     */
    public abstract ResultType getType();

    /**
     * Compare the current result against the given one.
     * Return the proper exception to express the difference or null
     * @param other - The other SnapshotResult to compare against
     * @return A SnapshotTestException instance or null
     */
    public abstract SnapshotTestException compare(SnapshotResult other);

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SnapshotResult)) {
            return false;
        }

        SnapshotResult other = (SnapshotResult) obj;
        return compare(other) == null;
    }
}
