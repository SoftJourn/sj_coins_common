package com.softjourn.common.spring.properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReloadableBeansBeanPostProcessorTest {


    ReloadableBeansBeanPostProcessor postProcessor;

    @Mock
    PropertiesReloadingListener listener;

    Set beansSet;

    @RefreshableProperties
    public static class TestBean {

    }

    List<Object> beans;

    @Before
    public void init() {

        postProcessor = new ReloadableBeansBeanPostProcessor();

        beans = new ArrayList<>();

        beans.add(new Object());
        beans.add(listener);
        beans.add(new Object());
        beans.add(new Object());
        beans.add(new Object());
        beans.add(new Object());
        beans.add(new Object());
        beans.add(new TestBean());
        beans.add(new TestBean());

        doAnswer(invocation -> {
            beansSet = (Set) invocation.getArguments()[0];
            return null;
        }).when(listener).setObservableBeans(anySet());
    }

    @Test
    public void postProcessBeforeInitialization() throws Exception {
        for (Object o : beans) {
            Object after = postProcessor.postProcessBeforeInitialization(o, "");
            assertTrue(o == after); // should always return same object
        }
    }

    @Test
    public void postProcessAfterInitialization() throws Exception {
        for (Object o : beans) {
            Object after = postProcessor.postProcessAfterInitialization(o, "");
            assertTrue(o == after); // should always return same object
        }

        verify(listener, times(1)).setObservableBeans(anySet());
        assertEquals(2, beansSet.size());
    }

}
