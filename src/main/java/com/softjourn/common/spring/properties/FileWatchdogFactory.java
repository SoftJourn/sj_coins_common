package com.softjourn.common.spring.properties;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Creates new instances of FileWatchdog
 */
@Component
public class FileWatchdogFactory {

    public FileWatchdog get(Path filePath, Consumer<Path> onChangeCallback) {
        return new FileWatchdog(filePath, onChangeCallback);
    }
}
