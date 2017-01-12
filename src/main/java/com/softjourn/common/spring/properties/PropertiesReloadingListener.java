package com.softjourn.common.spring.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
class PropertiesReloadingListener {

    private final static Pattern pattern = Pattern.compile("file:(.*)]$");

    private final AbstractEnvironment environment;

    private PropertiesUpdater propertiesUpdater;

    private Set<String> sources = new HashSet<>();

    private Set<FileWatchdog> watchers;

    @Autowired
    public PropertiesReloadingListener(AbstractEnvironment environment) {
        this.environment = environment;
    }


    void setObservableBeans(Set<Object> observableBeans) {
        propertiesUpdater = new PropertiesUpdater(observableBeans);
    }

    @PostConstruct
    private void init() {
        try {
            setupSources();
            setupWatching();
        } catch (Exception e) {
            log.warn("Error during initialization PropertiesReloadingListener. Properties reloading will not work.");
        }
    }

    @PreDestroy
    private void tearDown() {
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
                .map(path -> new FileWatchdog(path, onChangeListener()))
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
        Properties properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(filePath));
            propertiesUpdater.update(properties);
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
