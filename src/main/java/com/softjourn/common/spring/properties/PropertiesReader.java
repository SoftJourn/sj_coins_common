package com.softjourn.common.spring.properties;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Reads properties from file
 */
@Component
public class PropertiesReader {

    public Properties read(String fileName) throws IOException {
        return read(Paths.get(fileName));
    }

    public Properties read(Path path) throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newBufferedReader(path));
        return properties;
    }
}

