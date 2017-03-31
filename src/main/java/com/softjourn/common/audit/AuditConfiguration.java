package com.softjourn.common.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

@Configuration
@EnableJpaRepositories("com.softjourn.common.audit")
@EntityScan(basePackages = "com.softjourn.common.audit")
public class AuditConfiguration {

    @Autowired
    private AuditRepository repository;

    @Autowired
    private ApplicationContext applicationContext;

    private boolean saveValue() {
        return getAnnotationValue(EnableAudit::saveValue, false);
    }

    private <V> V getAnnotationValue(Function<EnableAudit, V> mapper, V defaultValue) {
        Collection<Object> beans = applicationContext.getBeansWithAnnotation(EnableAudit.class).values();
        if (beans.size() > 1) throw new IllegalStateException("Annotation @EnableAudit can be used only on one of configuration classes." );
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

    @PostConstruct
    private void init() {
        AuditEntityListener.setRepository(repository);
        AuditEntityListener.saveValue(saveValue());
    }
}
