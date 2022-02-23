package com.softjourn.common.spring.properties;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Bean post processor to create list of all beans
 * that needs to be notified on properties changed.
 * Is used by Spring by default.
 * Use annotation RefreshableProperties to add bean to this list.
 * Object to be processed should also be Spring bean (e.g. Component, Service, Controller)
 */
@Component
class ReloadableBeansBeanPostProcessor implements BeanPostProcessor {

  private final Set<Object> propertiesReloadingObservableBeans = new HashSet<>();

  @Override
  public Object postProcessBeforeInitialization(Object bean, String s) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
    if (bean instanceof PropertiesReloadingListener) {
      PropertiesReloadingListener listener = (PropertiesReloadingListener) bean;
      listener.setObservableBeans(propertiesReloadingObservableBeans);
    } else if (bean.getClass().isAnnotationPresent(RefreshableProperties.class)) {
      propertiesReloadingObservableBeans.add(bean);
    }
    return bean;
  }
}
