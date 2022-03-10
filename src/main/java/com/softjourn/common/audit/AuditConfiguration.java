package com.softjourn.common.audit;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.softjourn.common.audit")
@EntityScan("com.softjourn.common.audit")
@RequiredArgsConstructor
public class AuditConfiguration {

  private final AuditRepository repository;
  private final ApplicationContext applicationContext;

  @PostConstruct
  private void init() {
    AuditEntityListener.setRepository(repository);
    AuditEntityListener.saveValue(saveValue());
  }

  private boolean saveValue() {
    return getAnnotationValue(EnableAudit::saveValue, false);
  }

  private <V> V getAnnotationValue(Function<EnableAudit, V> mapper, V defaultValue) {
    Collection<Object> beans = applicationContext
        .getBeansWithAnnotation(EnableAudit.class)
        .values();

    if (beans.size() > 1) {
      throw new IllegalStateException(
          "Annotation @EnableAudit can be used only on one of configuration classes." );
    }

    return beans.stream()
        .map(Object::getClass)
        .map(Class::getAnnotatedSuperclass)
        .map(clazz -> (Class)clazz.getType())
        .flatMap(aClass -> Stream.of(aClass.getDeclaredAnnotations()))
        .filter(annotation -> EnableAudit.class.equals(annotation.annotationType()))
        .map(annotation -> mapper.apply((EnableAudit)annotation))
        .findAny()
        .orElse(defaultValue);
  }
}
