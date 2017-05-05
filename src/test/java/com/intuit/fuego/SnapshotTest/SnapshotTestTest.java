package com.intuit.fuego.SnapshotTest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;

public class SnapshotTestTest {
    private SnapshotTest snapshotTest;

    @Before
    public void beforeEach() {
        snapshotTest = new SnapshotTest(new File("src/test/resources"));
    }

    @After
    public void afterEach() {
        System.setProperty("updateSnapshot", "");
    }

    private String getRandomString() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    private JsonElement getRandomJsonElement() {
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        arr.add(getRandomString());
        obj.add("randomThing", arr);
        obj.addProperty("otherRandomThing", getRandomString());
        return obj;
    }

    @Test
    public void workingStringSnapshot() throws Exception {
        snapshotTest.takeSnapshot("stringTest", "This is a sample thing that I want to store");
    }

    @Test(expected = SnapshotTestException.class)
    public void brokenStringSnapshot() throws Exception {
        snapshotTest.takeSnapshot("stringTest", "This is not a sample thing that I want to store");
    }

    @Test
    public void writingStringSnapshot() throws Exception {
        String testString = getRandomString();
        System.setProperty("updateSnapshot", "true");
        snapshotTest.takeSnapshot("stringWriteTest", testString);
        System.setProperty("updateSnapshot", "");
        snapshotTest.takeSnapshot("stringWriteTest", testString);
    }

    @Test
    public void workingNonExistantStringSnapshot() throws Exception {
        String testString = getRandomString();
        File createTestFile = new File("src/test/resources/stringCreateTest");
        if (createTestFile.exists()) {
            createTestFile.delete();
        }
        snapshotTest.takeSnapshot("stringCreateTest", testString);
        snapshotTest.takeSnapshot("stringCreateTest", testString);
    }

    @Test
    public void writingJSONSnapshot() throws Exception {
        JsonElement testElement = getRandomJsonElement();
        System.setProperty("updateSnapshot", "true");
        snapshotTest.takeSnapshot("jsonWriteTest", testElement);
        System.setProperty("updateSnapshot", "");
        snapshotTest.takeSnapshot("jsonWriteTest", testElement);
    }

    @Test
    public void workingJSONSnapshot() throws Exception {
        // {"randomThing":["firstRandomThing"],"otherRandomThing":"some other text"}
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        arr.add("firstRandomThing");
        obj.add("randomThing", arr);
        obj.addProperty("otherRandomThing", "some other text");

        snapshotTest.takeSnapshot("jsonTest", obj);
    }

    @Test(expected = SnapshotTestException.class)
    public void brokenJSONSnapshot() throws Exception {
        // {"randomThing":["firstRandomThing"],"otherRandomThing":"some other text"}
        JsonObject obj = new JsonObject();
        JsonArray arr = new JsonArray();
        arr.add("definitely not the same thing");
        obj.add("randomThing", arr);
        obj.addProperty("otherRandomThing", "some other text");

        snapshotTest.takeSnapshot("jsonTest", obj);
    }
}