package com.softjourn.common.spring.aspects.logging;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to include logging aspect into spring context.
 */
@Configuration
@ComponentScan("com.softjourn.common.spring.aspects.logging")
public class LoggingAspectConfiguration {
}
