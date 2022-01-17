package com.softjourn.common.spring.properties;

/*
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.Reader;
import java.nio.file.Paths;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;
*/

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.Reader;
import java.nio.file.Paths;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

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
        verify(properties).load(any(Reader.class));
    }

}
