# snapshot4j [![Build Status](https://travis-ci.org/adierkens/snapshot4j.svg?branch=master)](https://travis-ci.org/adierkens/snapshot4j) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.adamdierkens/snapshot4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.adamdierkens/snapshot4j)

> A snapshot testing library for Java


## Usage

``` xml
    <dependency>
        <groupId>com.adamdierkens</groupId>
        <artifactId>snapshot4j</artifactId>
    </dependency>
```


```java
SnapshotTest snapshotTest = new SnapshotTest(File snapshotPath);
snapshotTest.takeSnapshot(String testName, String|JsonElement testInput); // throws SnapshotTestException

// or

SnapshotTest.takeSnapshot(File snapshotPath, String testName, String|JsonElement testInput); // throws SnapshotTestException

```

