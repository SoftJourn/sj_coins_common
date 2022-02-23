package com.softjourn.common.spring.properties;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to include properties reloading watching into spring context.
 */
@Configuration
@ComponentScan("com.softjourn.common.spring.properties")
public class PropertiesReloadingConfiguration {
}
