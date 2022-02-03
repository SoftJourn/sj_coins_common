package com.softjourn.common.spring.properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;


public class FileWatchdogTest {

    private static final String LOCAL_FILE_NAME = "test.file";
    private static final String ABSOLUTE_FILE_NAME = System.getProperty("user.home") + "/test.file";

    private FileWatchdog watchdog;

    private AtomicInteger counter;

    @Before
    public void setUp() throws Exception {
        counter = new AtomicInteger(0);
        createFile(LOCAL_FILE_NAME);
        createFile(ABSOLUTE_FILE_NAME);
    }

    @Test
    public void testLocalFileChanged() throws Exception {
        check(LOCAL_FILE_NAME);
    }

    @Test
    public void testAbsoluteFileChanged() throws Exception {
        check(ABSOLUTE_FILE_NAME);
    }

    @After
    public void tearDown() throws Exception {
        deleteFile(LOCAL_FILE_NAME);
        deleteFile(ABSOLUTE_FILE_NAME);
    }

    private void check(String filename) throws Exception {
        watchdog = new FileWatchdog(Paths.get(filename), path -> counter.incrementAndGet(), 1, TimeUnit.SECONDS);
        Thread.sleep(100); //wait for initializing
        changeFile(filename);
        Thread.sleep(11000); //wait for event
        assertTrue(counter.get() > 0);
    }

    private void createFile(String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
        writer.write("test");
        writer.close();
    }

    private void changeFile(String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
        writer.write("changed");
        writer.close();
    }

    private void deleteFile(String fileName) {
        new File(fileName).deleteOnExit();
    }

}