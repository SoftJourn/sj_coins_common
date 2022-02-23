package com.softjourn.common.spring.properties;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

class FileWatchdog implements AutoCloseable {

  private final Path dirPath;
  private final Path filePath;
  private final Consumer<Path> onChangeCallback;
  private final ScheduledExecutorService listenerExecutor =
      Executors.newSingleThreadScheduledExecutor();
  private final WatchKey watchKey;

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
      throw new RuntimeException(
          "Error during initializing FileWatchdog. " + ioe.getMessage(), ioe);
    }
  }

  private Runnable listenerWorker() {
    return () -> Stream.of(watchKey)
        .flatMap(watchKey -> watchKey.pollEvents().stream())
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
