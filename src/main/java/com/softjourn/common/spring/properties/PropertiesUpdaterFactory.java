package com.softjourn.common.spring.properties;

import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class PropertiesUpdaterFactory {

    public PropertiesUpdater get(Set<Object> beans) {
        return new PropertiesUpdater(beans);
    }
}
