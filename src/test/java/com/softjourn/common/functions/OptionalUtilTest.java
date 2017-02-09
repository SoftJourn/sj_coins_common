package com.softjourn.common.functions;

import lombok.AllArgsConstructor;

import lombok.Getter;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OptionalUtilTest {

    @Getter
    @AllArgsConstructor
    private static class TestObject {
        @Getter
        private String val;
    }

    @Test
    public void notNull_CorrectValue() throws Exception {
        Optional<String> value = Optional.of(new TestObject("test"))
                .flatMap(OptionalUtil.nullable(TestObject::getVal));

        assertTrue(value.isPresent());
    }

    @Test
    public void notNull_NullValue() throws Exception {
        Optional<String> value = Optional.of(new TestObject(null))
                .flatMap(OptionalUtil.nullable(TestObject::getVal));

        assertFalse(value.isPresent());
    }



}