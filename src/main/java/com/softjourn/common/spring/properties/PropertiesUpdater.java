package com.softjourn.common.spring.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Class that provide updating bean's fields accordingly with specified properties.
 */
@Slf4j
class PropertiesUpdater {

    private static final Pattern PATTERN = Pattern.compile("\\$\\{(.*)\\}");

    private final Set<Object> observableBeans;

    PropertiesUpdater(Set<Object> observableBeans) {
        this.observableBeans = observableBeans;
    }

    void update(Properties properties) {
        observableBeans.forEach(bean -> {
            synchronized (bean) {
                setProperties(bean, properties);
                runUpdate(bean);
            }
        });
    }

    private void setProperties(Object bean, Properties properties) {
        Stream.of(bean.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Value.class))
                .forEach(field -> setProperty(field, bean, properties));
    }

    private void runUpdate(Object bean) {
        Stream.of(bean.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(OnPropertiesUpdate.class))
                .peek(method -> method.setAccessible(true))
                .findFirst()
                .ifPresent(method -> callMethod(method, bean));
    }

    private void callMethod(Method method, Object bean) {
        try {
            method.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.warn("Can't run bean method " + method.getName() + " for bean " + bean + ". " + e.getMessage());
        }
    }

    private void setProperty(Field field, Object bean, Properties properties) {
        try {
            String propertyName = getPropertyName(field);
            if (properties.containsKey(propertyName)) {
                field.setAccessible(true);
                field.set(bean, properties.getProperty(propertyName));
            }
        } catch (IllegalAccessException ignored) {
            //should never happen
        }
    }

    private String getPropertyName(Field field) {
        String annotationValue = field.getAnnotation(Value.class).value();
        return getPropertyName(annotationValue);
    }

    private String getPropertyName(String annotationValue) {
        Matcher matcher = PATTERN.matcher(annotationValue);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }


}
