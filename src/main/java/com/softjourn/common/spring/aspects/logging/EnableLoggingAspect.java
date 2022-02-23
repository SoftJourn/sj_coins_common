package com.softjourn.common.spring.aspects.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Place this annotation on your Configuration bean
 * to enable debug logging of all spring beans in service package
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LoggingAspectConfiguration.class)
public @interface EnableLoggingAspect {
}
