package com.softjourn.common.spring.properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.Reader;
import java.nio.file.Paths;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Properties.class, PropertiesReader.class})
public class PropertiesReaderTest {

    private PropertiesReader reader;

    private Properties properties;

    @Before
    public void setUp() throws Exception {
        mockStatic(Properties.class);
        reader = new PropertiesReader();

        properties = mock(Properties.class);
    }

    @Test
    public void read() throws Exception {
        whenNew(Properties.class).withNoArguments().thenReturn(properties);
        doNothing().when(properties).load(any(Reader.class));
        reader.read(Paths.get("."));

        Mockito.verify(properties).load(any(Reader.class));
    }

}