package com.adamdierkens.snapshot4j.result;

import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

public class JsonResultTest {

    @Test
    public void compareSameObjectsWithDiffKeyOrder() throws Exception {

        JsonObject obj1 = new JsonObject();
        obj1.addProperty("foo", 1);
        obj1.addProperty("bar", 2);

        JsonObject obj2 = new JsonObject();
        obj2.addProperty("bar", 2);
        obj2.addProperty("foo", 1);

        JsonResult result1 = new JsonResult(obj1);
        JsonResult result2 = new JsonResult(obj2);

        Assert.assertNull(result1.compare(result2));
    }

}