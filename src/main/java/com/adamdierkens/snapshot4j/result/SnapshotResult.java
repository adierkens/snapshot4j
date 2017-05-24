package com.adamdierkens.snapshot4j.result;

import com.adamdierkens.snapshot4j.SnapshotTest.SnapshotTestException;
import com.google.gson.JsonElement;

public abstract class SnapshotResult {

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

    /**
     * Gets a JSON serializable version of the result
     * @return - a JsonElement instance
     */
    public abstract JsonElement toJson();

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SnapshotResult)) {
            return false;
        }

        SnapshotResult other = (SnapshotResult) obj;
        return compare(other) == null;
    }
}
