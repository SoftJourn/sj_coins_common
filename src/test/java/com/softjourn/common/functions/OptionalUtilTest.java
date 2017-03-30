package com.softjourn.common.functions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

import static org.junit.Assert.*;

public class OptionalUtilTest {

    @Getter
    @AllArgsConstructor
    private static class TestObject {
        @Getter
        private String val;
    }

    @org.junit.Test
    public void notNull_CorrectValue() throws Exception {
        Optional<String> value = Optional.of(new TestObject("test"))
                .flatMap(OptionalUtil.nullable(TestObject::getVal));

        assertTrue(value.isPresent());
    }

    @org.junit.Test
    public void notNull_NullValue() throws Exception {
        Optional<String> value = Optional.of(new TestObject(null))
                .flatMap(OptionalUtil.nullable(TestObject::getVal));

        assertFalse(value.isPresent());
    }

    @org.junit.Test
    public void allChainOrElse_firstLevelValueNull_otherwise() {
        assertNull(OptionalUtil.allChainOrElse(null, Test::getInnerTest, InnerTest::getInnerInnerTest, InnerInnerTest::getId, null));
        assertEquals(10, OptionalUtil.allChainOrElse(null, Test::getInnerTest, InnerTest::getInnerInnerTest, InnerInnerTest::getId, 10));
    }

    @org.junit.Test
    public void allChainOrElse_secondLevelValueNull_otherwise() {
        Test test = new Test("10", null);

        assertNull(OptionalUtil.allChainOrElse(test, Test::getInnerTest, InnerTest::getInnerInnerTest, InnerInnerTest::getId, null));
        assertEquals("10", OptionalUtil.allChainOrElse(test, Test::getInnerTest, InnerTest::getInnerInnerTest, InnerInnerTest::getId, "10"));
    }

    @org.junit.Test
    public void allChainOrElse_thirdLevelValueNull_otherwise() {
        InnerTest innerTest = new InnerTest("100", null);
        Test test = new Test("10", innerTest);

        assertNull(OptionalUtil.allChainOrElse(test, Test::getInnerTest, InnerTest::getInnerInnerTest, InnerInnerTest::getId, null));
        assertEquals("10", OptionalUtil.allChainOrElse(test, Test::getInnerTest, InnerTest::getInnerInnerTest, InnerInnerTest::getId, "10"));
    }

    @org.junit.Test
    public void allChainOrElse_lastLevelValueNull_otherwise() {
        InnerInnerTest innerInnerTest = new InnerInnerTest(null);
        InnerTest innerTest = new InnerTest("100", innerInnerTest);
        Test test = new Test("10", innerTest);

        assertNull(OptionalUtil.allChainOrElse(test, Test::getInnerTest, InnerTest::getInnerInnerTest, InnerInnerTest::getId, null));
        assertEquals("10", OptionalUtil.allChainOrElse(test, Test::getInnerTest, InnerTest::getInnerInnerTest, InnerInnerTest::getId, "10"));
    }

    @org.junit.Test
    public void allChainOrElse_allLevelValuesPresented_expectedValue() {
        InnerInnerTest innerInnerTest = new InnerInnerTest("1000");
        InnerTest innerTest = new InnerTest("100", innerInnerTest);
        Test test = new Test("10", innerTest);

        assertEquals("1000", OptionalUtil.allChainOrElse(test, Test::getInnerTest, InnerTest::getInnerInnerTest, InnerInnerTest::getId, "1000"));
    }

    @org.junit.Test
    public void allChainOrElse_oneLevel_expectedValue() {
        InnerInnerTest innerInnerTest = new InnerInnerTest("1000");
        InnerTest innerTest = new InnerTest("100", innerInnerTest);
        Test test = new Test("10", innerTest);

        assertEquals("10", OptionalUtil.allChainOrElse(test, Test::getId, "1000000000"));
    }

    @org.junit.Test
    public void allChainOrElse_twoLevels_expectedValue() {
        InnerInnerTest innerInnerTest = new InnerInnerTest("1000");
        InnerTest innerTest = new InnerTest("100", innerInnerTest);
        Test test = new Test("10", innerTest);

        assertEquals("100", OptionalUtil.allChainOrElse(test, Test::getInnerTest, InnerTest::getId,"100000000000000"));
    }

    @AllArgsConstructor
    @Getter
    private static class Test {

        private String id;

        private InnerTest innerTest;
    }

    @AllArgsConstructor
    @Getter
    private static class InnerTest {
        private String id;
        private InnerInnerTest innerInnerTest;
    }

    @AllArgsConstructor
    @Getter
    private static class InnerInnerTest {
        private String id;
    }


}