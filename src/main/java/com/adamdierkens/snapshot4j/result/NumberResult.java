package com.adamdierkens.snapshot4j.result;

import com.adamdierkens.snapshot4j.SnapshotTest.SnapshotTestException;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class NumberResult extends SnapshotResult {

    private Number number;
    private Number delta;

    public NumberResult() {
        number = 0;
    }

    public NumberResult(Number num) {
        number = num;
    }

    public NumberResult(Number num, Number diff) {
        number = num;
        delta = diff;
    }

    public NumberResult(JsonResult result) {
        JsonElement ele = result.toJson();
        if (ele.isJsonPrimitive()) {
            JsonPrimitive otherPrimative = ele.getAsJsonPrimitive();
            if (otherPrimative.isNumber()) {
                number = otherPrimative.getAsNumber();
            }
        }
    }

    @Override
    public ResultType getType() {
        return ResultType.Number;
    }

    @Override
    public SnapshotTestException compare(SnapshotResult other) {
        NumberResult otherNumber;
        if (other instanceof JsonResult) {
            otherNumber = new NumberResult((JsonResult) other);
        } else if (other instanceof NumberResult) {
            otherNumber = (NumberResult) other;
        } else {
            return new SnapshotTestException(getType(), other.getType());
        }

        double diff = 0;

        if (delta != null) {
            diff = delta.doubleValue();
        } else if (otherNumber.delta != null) {
            diff = otherNumber.delta.doubleValue();
        }

        Double low = number.doubleValue() - diff;
        Double high = number.doubleValue() + diff;

        if (otherNumber.number.doubleValue() >= low && otherNumber.number.doubleValue() <= high) {
            return null;
        }

        return new SnapshotTestException(number, otherNumber.number, diff);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(number);
    }
}
