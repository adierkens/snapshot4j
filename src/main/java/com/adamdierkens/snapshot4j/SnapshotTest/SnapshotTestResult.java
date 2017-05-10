package com.adamdierkens.snapshot4j.SnapshotTest;

import com.google.gson.JsonElement;

class SnapshotTestResult {

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

    private JsonElement getResultElement() {
        return resultElement;
    }

    private String getResultString() {
        return resultString;
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

            return new SnapshotTestException(String.format("Got json object %s but expected %s", other.resultElement, this.resultElement));

        } else if (this.resultType.equals(SnapshotTestResultType.String)) {

            if (resultString.equals(other.resultString)) {
                return null;
            }

            return new SnapshotTestException(String.format("Got string %s but expected %s", other.resultString, this.resultString));
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
            return resultElement.toString();
        }
    }
}