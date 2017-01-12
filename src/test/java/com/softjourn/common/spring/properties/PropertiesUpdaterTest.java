package com.softjourn.common.spring.properties;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class PropertiesUpdaterTest {

    private static final String DEFAULT_FIELD_VALUE = "def";
    private static final String NEW_FIELD_VALUE = "new";
    private static final String PROPERTY_NAME = "test.property";

    public static class TestBean {
        @Value("${" + PROPERTY_NAME + "}")
        private String updatableField;

        private String unupdatableField;

        private int counter = 0;

        TestBean(String unupdatableField, String updatableField) {
            this.unupdatableField = unupdatableField;
            this.updatableField = updatableField;
        }

        @OnPropertiesUpdate
        private void UpdatableMethod() {
            counter++;
        }

        public String getUpdatableField() {
            return updatableField;
        }

        public String getUnupdatableField() {
            return unupdatableField;
        }
    }

    public static class TestBean1 {
        @Value("${unknown.property}")
        private String updatableField;

        private String unupdatableField;

        TestBean1(String unupdatableField, String updatableField) {
            this.unupdatableField = unupdatableField;
            this.updatableField = updatableField;
        }

        public String getUpdatableField() {
            return updatableField;
        }

        public String getUnupdatableField() {
            return unupdatableField;
        }
    }

    private TestBean testBean;
    private TestBean1 testBean1;

    private PropertiesUpdater updater;

    private Properties properties;

    @Before
    public void setUp() throws Exception {
        properties = new Properties();
        properties.setProperty(PROPERTY_NAME, NEW_FIELD_VALUE);

        testBean = new TestBean(DEFAULT_FIELD_VALUE, DEFAULT_FIELD_VALUE);
        testBean1 = new TestBean1(DEFAULT_FIELD_VALUE, DEFAULT_FIELD_VALUE);

        Set<Object> beans = new HashSet<Object>(){{
            add(testBean);
            add(testBean1);
        }};

        updater = new PropertiesUpdater(beans);
    }

    @Test
    public void update() throws Exception {
        updater.update(properties);

        assertEquals(NEW_FIELD_VALUE, testBean.updatableField);

        assertEquals(DEFAULT_FIELD_VALUE, testBean.unupdatableField);
        assertEquals(DEFAULT_FIELD_VALUE, testBean1.updatableField);
        assertEquals(DEFAULT_FIELD_VALUE, testBean1.unupdatableField);

        assertEquals(1, testBean.counter);

    }

}