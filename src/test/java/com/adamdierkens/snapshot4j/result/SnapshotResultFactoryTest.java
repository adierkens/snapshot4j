package com.adamdierkens.snapshot4j.result;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class SnapshotResultFactoryTest {
    @Test
    public void readEmpty() throws Exception {
        Assert.assertEquals(ResultType.Empty, SnapshotResultFactory.read(new File("src/test/resources"),"stringEmpty").getType());
        Assert.assertEquals(ResultType.Empty, SnapshotResultFactory.read(new File("src/test/resources"),"stringEmpty", "snapshotName").getType());
    }

}