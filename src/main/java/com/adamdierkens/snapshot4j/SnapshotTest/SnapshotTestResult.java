package com.adamdierkens.snapshot4j.SnapshotTest;

import com.google.gson.*;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.fusesource.jansi.Ansi;

import java.util.*;

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

    private void appendLines(List<String> lines, String prefix, StringBuilder stringBuilder, Ansi.Color color) {
        String _prefix = "";
        for (String line : lines) {
            stringBuilder.append(_prefix);
            stringBuilder.append(Ansi.ansi().fg(color).a(prefix).a(line).reset().toString());
            _prefix = "\n";
        }
    }

    private JsonElement sort(JsonElement ele) {
        if (ele.isJsonObject()) {

            JsonObject object = ele.getAsJsonObject();
            List<String> keys = new ArrayList<>();

            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                keys.add(entry.getKey());
            }

            Collections.sort(keys);
            JsonObject newObj = new JsonObject();

            for (String key : keys) {
                newObj.add(key, sort(object.get(key)));
            }

            return newObj;

        } else if (ele.isJsonArray()) {
            JsonArray array = ele.getAsJsonArray();
            for (int i=0; i<array.size(); i++) {
                array.set(i, sort(array.get(i)));
            }
            return array;
        }
        return ele;
    }

    SnapshotTestException compare(SnapshotTestResult other) {
        if (!other.resultType.equals(resultType)) {
            return new SnapshotTestException(String.format("Result types differ. Got %s but expected %s", other.resultType, this.resultType));
        }

        if (resultType.equals(SnapshotTestResultType.JSON)) {

            if (resultElement.equals(other.resultElement)) {
                return null;
            }

            Gson prettyPrintGson = new GsonBuilder().setPrettyPrinting().create();
            String left = prettyPrintGson.toJson(sort(this.resultElement));
            String right = prettyPrintGson.toJson(sort(other.resultElement));

            List<String> leftLines = Arrays.asList(left.split("\n"));
            List<String> rightLines = Arrays.asList(right.split("\n"));

            Patch<String> patch = DiffUtils.diff(leftLines, rightLines);

            StringBuilder result = new StringBuilder("\n");

            List<Delta<String>> deltas = patch.getDeltas();

            for (Integer line=0; line<leftLines.size(); line++) {

                boolean alreadyPrinted = false;

                for (Delta<String> delta : deltas) {
                    if (delta.getOriginal().getPosition() == line) {
                        if (delta.getType().equals(Delta.TYPE.CHANGE)) {
                            appendLines(delta.getOriginal().getLines(), "-", result, Ansi.Color.RED);
                            result.append("\n");
                            appendLines(delta.getRevised().getLines(), "+", result, Ansi.Color.GREEN);
                            alreadyPrinted = true;
                        } else if (delta.getType().equals(Delta.TYPE.DELETE)) {
                            appendLines(delta.getOriginal().getLines(), "-", result, Ansi.Color.RED);
                            alreadyPrinted = true;
                        } else if (delta.getType().equals(Delta.TYPE.INSERT)) {
                            appendLines(delta.getRevised().getLines(), "+", result, Ansi.Color.GREEN);
                            result.append("\n");
                        }

                        deltas.remove(delta);
                    }
                }

                if (!alreadyPrinted) {
                    result.append(" ");
                    result.append(leftLines.get(line));
                }
                result.append("\n");
            }

            return new SnapshotTestException(result.toString());

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