package com.softjourn.common.spring.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Place this annotation on your Configuration bean to enable properties reloading listening
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(PropertiesReloadingConfiguration.class)
public @interface EnablePropertiesReloading {
}
