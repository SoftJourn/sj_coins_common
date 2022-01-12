package com.softjourn.common.spring.properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesReloadingListenerTest {

    private static final String PROPERTIES_FILE_NAME = "/home/test/test.properties";

    @Mock
    private
    FileWatchdogFactory watchdogFactory;
    @Mock
    private
    PropertiesUpdater updater;
    @Mock
    private
    PropertiesUpdaterFactory propertiesUpdaterFactory;
    @Mock
    private
    FileWatchdog watchdog;
    @Mock
    private
    AbstractEnvironment environment;
    @Mock
    private
    PropertiesPropertySource propertySource;

    @Mock
    private PropertiesReader reader;

    private Consumer<Path> callback;

    @Before
    public void setUp() throws Exception {
        when(watchdogFactory.get(any(), any())).thenAnswer(invoke -> {
            callback = (Consumer<Path>) invoke.getArguments()[1];
            return watchdog;
        });
        when(propertiesUpdaterFactory.get(any())).thenReturn(updater);

        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addFirst(propertySource);

        when(propertySource.getName()).thenReturn("properties [file:" + PROPERTIES_FILE_NAME + "]");

        when(environment.getPropertySources()).thenReturn(propertySources);

        when(reader.read(any(Path.class))).thenReturn(new Properties());
    }

    @Test
    public void test() {
        PropertiesReloadingListener listener = new PropertiesReloadingListener(environment, watchdogFactory, propertiesUpdaterFactory, reader);
        listener.init();
        listener.setObservableBeans(Collections.singleton(new Object()));

        verify(watchdogFactory, times(1)).get(eq(Paths.get(PROPERTIES_FILE_NAME)), any());
    }

    @Test
    public void testTearDown() throws Exception {
        PropertiesReloadingListener listener = new PropertiesReloadingListener(environment, watchdogFactory, propertiesUpdaterFactory, reader);
        listener.init();
        listener.setObservableBeans(Collections.singleton(new Object()));
        listener.tearDown();

        verify(watchdog, times(1)).close();
    }

    @Test
    public void testFileChanged() {
        PropertiesReloadingListener listener = new PropertiesReloadingListener(environment, watchdogFactory, propertiesUpdaterFactory, reader);
        listener.init();
        listener.setObservableBeans(Collections.singleton(new Object()));

        callback.accept(Paths.get(PROPERTIES_FILE_NAME));

        verify(updater, times(1)).update(any());
    }

    @Test
    public void testFileChangedButThenDeleted() throws IOException {
        PropertiesReloadingListener listener = new PropertiesReloadingListener(environment, watchdogFactory, propertiesUpdaterFactory, reader);
        listener.init();
        listener.setObservableBeans(Collections.singleton(new Object()));
        when(reader.read(any(Path.class))).thenThrow(new IOException());

        callback.accept(Paths.get(PROPERTIES_FILE_NAME));

        verify(updater, never()).update(any()); //updater is not called
    }
}
