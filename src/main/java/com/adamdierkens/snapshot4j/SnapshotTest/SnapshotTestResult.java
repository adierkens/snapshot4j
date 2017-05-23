package com.adamdierkens.snapshot4j.SnapshotTest;

import com.adamdierkens.snapshot4j.diff.SnapshotDiff;
import com.adamdierkens.snapshot4j.diff.SnapshotDiffJson;
import com.adamdierkens.snapshot4j.diff.SnapshotDiffText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

class SnapshotTestResult {

    private static final Gson PP_GSON = new GsonBuilder().setPrettyPrinting().create();

    enum SnapshotTestResultType {
        JSON,
        String,
        Empty
    }

    private JsonElement resultElement;
    private String resultString;
    private SnapshotTestResultType resultType;

    SnapshotTestResult() {
        this.resultType = SnapshotTestResultType.Empty;
    }

    SnapshotTestResult(String result) {
        this.resultString = result;
        this.resultType = SnapshotTestResultType.String;
    }

    SnapshotTestResult(JsonElement result) {
        this.resultElement = result;
        this.resultType = SnapshotTestResultType.JSON;
    }

    SnapshotTestResultType getResultType() {
        return resultType;
    }

    SnapshotTestException compare(SnapshotTestResult other) {
        if (!other.resultType.equals(resultType)) {
            return new SnapshotTestException(String.format("Result types differ. Got %s but expected %s", other.resultType, this.resultType));
        }

        if (resultType.equals(SnapshotTestResultType.JSON)) {

            if (resultElement.equals(other.resultElement)) {
                return null;
            }

            SnapshotDiff diff = new SnapshotDiffJson(resultElement, other.resultElement);
            return new SnapshotTestException(diff.prettyPrintDiff());

        } else if (this.resultType.equals(SnapshotTestResultType.String)) {

            if (resultString.equals(other.resultString)) {
                return null;
            }

            SnapshotDiff diff = new SnapshotDiffText(resultString, other.resultString);
            return new SnapshotTestException(diff.prettyPrintDiff());
        }

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof SnapshotTestResult)) {
            return false;
        }

        SnapshotTestResult other = (SnapshotTestResult) obj;

        return compare(other) == null;
    }

    @Override
    public String toString() {
        if (resultType.equals(SnapshotTestResultType.Empty)) {
            return null;
        } else if (resultType.equals(SnapshotTestResultType.String)) {
            return resultString;
        } else {
            return PP_GSON.toJson(resultElement);
        }
    }
}