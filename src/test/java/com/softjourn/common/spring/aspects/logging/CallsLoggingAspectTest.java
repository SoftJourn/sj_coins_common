package com.softjourn.common.spring.aspects.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CallsLoggingAspectTest {

    private CallsLoggingAspect loggingAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature signature;

    @Mock
    org.slf4j.Logger logger;

    @Before
    public void setUp() throws Exception {

        loggingAspect = new CallsLoggingAspect();

        Field log = loggingAspect.getClass().getDeclaredField("log");
        setFinalStatic(log, logger);

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("test");
        when(signature.getDeclaringType()).thenReturn(this.getClass());
    }

    @Test(expected = Exception.class)
    public void logCall_exception_debugEnabled() throws Throwable {
        when(joinPoint.proceed()).thenThrow(new Exception("test exception"));
        when(logger.isDebugEnabled()).thenReturn(true);
        loggingAspect.logCall(joinPoint);


        verify(logger).debug(argThat(argument -> {
            return argument.startsWith("Call of method");
        }));

        verify(logger).debug(argThat(argument -> {
            return argument.contains("failed with exception") && argument.contains("test exception");
        }));
    }

    @Test
    public void logCall_return_debugEnabled() throws Throwable {
        when(joinPoint.proceed()).thenReturn(this);
        when(logger.isDebugEnabled()).thenReturn(true);
        assertEquals(this, loggingAspect.logCall(joinPoint));


        verify(logger).debug(argThat(argument -> {
            return argument.startsWith("Call of method");
        }));

        verify(logger).debug(argThat(argument -> {
            return argument.startsWith("Return value of method") && argument.contains(this.getClass().getSimpleName());
        }));
    }

    @Test
    public void logCall_return_debugDisabled() throws Throwable {
        when(joinPoint.proceed()).thenReturn(this);
        when(logger.isDebugEnabled()).thenReturn(false);
        assertEquals(this, loggingAspect.logCall(joinPoint));

        verify(logger, never()).debug(anyString());
    }

    @Test
    public void matchNamesWithArgs_empty() throws Exception {
        assertEquals("[]", loggingAspect.matchNamesWithArgs(new String[0], new String[0]));
    }

    @Test
    public void matchNamesWithArgs_null() throws Exception {
        assertEquals("[]", loggingAspect.matchNamesWithArgs(null, new String[0]));
        assertEquals("[]", loggingAspect.matchNamesWithArgs(new String[0], null));
        assertEquals("[]", loggingAspect.matchNamesWithArgs(null, null));
    }

    @Test
    public void matchNamesWithArgs_goodCase() throws Exception {
        assertEquals("[account=testAccount]", loggingAspect.matchNamesWithArgs(new String[]{"account"}, new String[]{"testAccount"}));
    }

    @Test
    public void matchNamesWithArgs_nullArgs() throws Exception {
        assertEquals("[account=null]", loggingAspect.matchNamesWithArgs(new String[]{"account"}, new String[]{null}));
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

}
