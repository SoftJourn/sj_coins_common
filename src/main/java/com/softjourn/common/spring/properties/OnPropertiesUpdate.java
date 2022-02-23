package com.softjourn.common.spring.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark Spring bean method to be triggered after property file is updated and
 * fields annotated by @Value annotation is updated.
 *
 * Method should accept no arguments.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnPropertiesUpdate {
}
