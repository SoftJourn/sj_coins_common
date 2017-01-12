package com.softjourn.common.spring.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Mark Spring bean that contains fields annotated by @Value annotation
 * for reloading this field value on updating property file in run time.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RefreshableProperties {
}
