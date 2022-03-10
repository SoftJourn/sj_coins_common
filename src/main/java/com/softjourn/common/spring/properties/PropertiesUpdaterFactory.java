package com.softjourn.common.spring.properties;

import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class PropertiesUpdaterFactory {

  public PropertiesUpdater get(Set<Object> beans) {
    return new PropertiesUpdater(beans);
  }
}
