package com.softjourn.common.spring.properties;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class PropertiesReloadingListener {

  private static final Pattern pattern = Pattern.compile("file:(.*)]$");

  private final AbstractEnvironment environment;
  private final FileWatchdogFactory watchdogFactory;
  private final PropertiesUpdaterFactory propertiesUpdaterFactory;
  private final PropertiesReader reader;
  private final Set<String> sources = new HashSet<>();

  private PropertiesUpdater propertiesUpdater;
  private Set<FileWatchdog> watchers;

  @Autowired
  public PropertiesReloadingListener(AbstractEnvironment environment,
      FileWatchdogFactory watchdogFactory,
      PropertiesUpdaterFactory propertiesUpdaterFactory,
      PropertiesReader reader) {
    this.environment = environment;
    this.watchdogFactory = watchdogFactory;
    this.propertiesUpdaterFactory = propertiesUpdaterFactory;
    this.reader = reader;
  }

  void setObservableBeans(Set<Object> observableBeans) {
    propertiesUpdater = propertiesUpdaterFactory.get(observableBeans);
  }

  @PostConstruct
  void init() {
    try {
      setupSources();
      setupWatching();
    } catch (Exception e) {
      log.warn("Error during initialization PropertiesReloadingListener. "
          + "Properties reloading will not work.");
    }
  }

  @PreDestroy
  void tearDown() {
    watchers.forEach(watcher -> {
      try {
        watcher.close();
      } catch (Exception ignored) {
        //do nothing
      }
    });
  }

  private void setupWatching() {
    watchers = sources.stream()
        .map(fileName -> Paths.get(fileName))
        .map(path -> watchdogFactory.get(path, onChangeListener()))
        .collect(Collectors.toSet());
  }

  private Consumer<Path> onChangeListener() {
    return path -> sources.stream()
        .map(fileName -> Paths.get(fileName).toAbsolutePath().normalize())
        .filter(current -> current.equals(path))
        .findAny()
        .ifPresent(this::updateProperties);
  }

  private void updateProperties(Path filePath) {
    try {
      propertiesUpdater.update(reader.read(filePath));
    } catch (IOException e) {
      log.warn("Can't reload properties from file " + filePath);
    }
  }

  private void setupSources() {
    for (PropertySource ps : environment.getPropertySources()) {
      if (ps instanceof PropertiesPropertySource && ps.getName().matches(".*file:.*")) {
        String propertiesFileName = retrieveFilePath(ps.getName());
        if (propertiesFileName != null) {
          sources.add(propertiesFileName);
        }
      }
    }
  }

  private String retrieveFilePath(String name) {
    Matcher matcher = pattern.matcher(name);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }
}
