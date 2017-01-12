package com.softjourn.common.spring.properties;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.*;

class FileWatchdog implements AutoCloseable {

    private final Path dirPath;

    private final Path filePath;

    private final Consumer<Path> onChangeCallback;

    private ScheduledExecutorService listenerExecutor = Executors.newSingleThreadScheduledExecutor();

    private WatchKey watchKey;

    FileWatchdog(Path filePath, Consumer<Path> onChangeCallback) {
        this(filePath, onChangeCallback, 5, TimeUnit.SECONDS);
    }

    FileWatchdog(Path filePath, Consumer<Path> onChangeCallback, int period, TimeUnit timeUnit) {
        try {
            this.filePath = filePath.toAbsolutePath().normalize();
            this.dirPath = filePath.getParent() == null ? Paths.get("./") : filePath.getParent();
            this.onChangeCallback = onChangeCallback;
            WatchService watcher = FileSystems.getDefault().newWatchService();
            watchKey = dirPath.register(watcher, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
            listenerExecutor.scheduleAtFixedRate(listenerWorker(), period, period, timeUnit);
        } catch (IOException ioe) {
            throw new RuntimeException("Error during initializing FileWatchdog. " + ioe.getMessage(), ioe);
        }
    }

    private Runnable listenerWorker() {
        return () -> Stream.of(watchKey)
                .flatMap(watchKey -> watchKey.pollEvents().stream())
                //.filter(watchEvent -> watchEvent.kind().equals(ENTRY_MODIFY))
                .filter(watchEvent -> watchEvent.count() > 0)
                .map(watchEvent -> (Path) watchEvent.context()) //relative path to watching directory
                .map(dirPath::resolve)
                .map(path -> path.toAbsolutePath().normalize())
                .filter(filePath::equals)
                .findAny()
                .ifPresent(onChangeCallback);
    }


    @Override
    public void close() throws Exception {
        watchKey.cancel();
        listenerExecutor.shutdown();
        listenerExecutor.awaitTermination(1, TimeUnit.SECONDS);
    }
}
