package com.softjourn.common.utils;

import org.junit.Test;

import java.math.BigInteger;
import java.time.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ReflectionUtilTest {

    @Test
    public void tryToCastValue_numbers() throws Exception {
        assertThat(ReflectionUtil.tryToCastValue(BigInteger.class, 52), is(BigInteger.valueOf(52)));
        assertThat(ReflectionUtil.tryToCastValue(BigInteger.class, "52"), is(BigInteger.valueOf(52)));

        assertThat(ReflectionUtil.tryToCastValue(Long.class, 52000000000L), is(52000000000L));
        assertThat(ReflectionUtil.tryToCastValue(Long.class, "52000000000"), is(52000000000L));

        assertThat(ReflectionUtil.tryToCastValue(Integer.class, 5200000), is(5200000));
        assertThat(ReflectionUtil.tryToCastValue(Integer.class, "5200000"), is(5200000));

        assertThat(ReflectionUtil.tryToCastValue(Short.class, 1000), is(Short.valueOf("1000")));
        assertThat(ReflectionUtil.tryToCastValue(Short.class, "1000"), is(Short.valueOf("1000")));

        assertThat(ReflectionUtil.tryToCastValue(Byte.class, 52), is((byte)52));
        assertThat(ReflectionUtil.tryToCastValue(Byte.class, "52"), is((byte)52));

        assertThat(ReflectionUtil.tryToCastValue(Double.class, 552.0), is(552.0));
        assertThat(ReflectionUtil.tryToCastValue(Double.class, 552), is(552.0));
        assertThat(ReflectionUtil.tryToCastValue(Double.class, "552.0"), is(552.0));
        assertThat(ReflectionUtil.tryToCastValue(Double.class, "552"), is(552.0));

        assertThat(ReflectionUtil.tryToCastValue(Float.class, 52.0), is(52.0f));
        assertThat(ReflectionUtil.tryToCastValue(Float.class, 52), is(52.0f));
        assertThat(ReflectionUtil.tryToCastValue(Float.class, "52.0"), is(52.0f));
        assertThat(ReflectionUtil.tryToCastValue(Float.class, "52"), is(52.0f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCastValue_numbers_notNumberString() throws Exception {
        assertThat(ReflectionUtil.tryToCastValue(BigInteger.class, "52test"), is(BigInteger.valueOf(52)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCastValue_numbers_FloatingValue() throws Exception {
        assertThat(ReflectionUtil.tryToCastValue(BigInteger.class, "52.0"), is(BigInteger.valueOf(52)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCastValue_integer_overflow() throws Exception {
        ReflectionUtil.tryToCastValue(Integer.class, 5200000000L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCastValue_integer_overflow_fromString() throws Exception {
        ReflectionUtil.tryToCastValue(Integer.class, "5200000000");
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCastValue_byte_overflow_fromString() throws Exception {
        ReflectionUtil.tryToCastValue(Byte.class, "160");
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCastValue_byte_overflow() throws Exception {
        ReflectionUtil.tryToCastValue(Byte.class, 198);
    }

    @Test
    public void tryToCastValue_time() throws Exception {
        assertThat(ReflectionUtil.tryToCastValue(Instant.class, "2017-02-02T00:00:00Z"), is(Instant.parse("2017-02-02T00:00:00Z")));
        assertThat(ReflectionUtil.tryToCastValue(LocalDate.class, "2017-02-02"), is(LocalDate.of(2017, Month.FEBRUARY, 2)));
        assertThat(ReflectionUtil.tryToCastValue(LocalTime.class, "15:05:00"), is(LocalTime.of(15, 5, 0)));
        assertThat(ReflectionUtil.tryToCastValue(LocalDateTime.class, "2017-02-02T15:05:00"), is(LocalDateTime.of(2017, Month.FEBRUARY, 2, 15, 5, 0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tryToCastValue_time_wrongFormat() throws Exception {
        ReflectionUtil.tryToCastValue(Instant.class, "2017.02.02 00:00:0");
    }

}