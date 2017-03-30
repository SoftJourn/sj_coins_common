package com.softjourn.common.audit;

import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

public class AuditEntityListener {

    private static AuditRepository repository;

    public static void setRepository(AuditRepository repository) {
        AuditEntityListener.repository = repository;
    }

    @PostPersist
    public void postCreate(Object entity) {
        AuditEntity auditEntity = getAuditEntity(entity, AuditEntity.Action.CREATE);
        repository.save(auditEntity);
    }

    @PostUpdate
    public void postUpdate(Object entity) {
        AuditEntity auditEntity = getAuditEntity(entity, AuditEntity.Action.UPDATE);
        repository.save(auditEntity);
    }

    @PostRemove
    public void postRemove(Object entity) {
        AuditEntity auditEntity = getAuditEntity(entity, AuditEntity.Action.REMOVE);
        repository.save(auditEntity);
    }

    AuditEntity getAuditEntity(Object entity, AuditEntity.Action action) {
        return AuditEntity.builder()
                .entityName(entity.getClass().getSimpleName())
                .action(action)
                .date(Instant.now())
                .userName(getAuditUserName())
                .entityId(getEntityId(entity))
                .build();
    }

    private String getEntityId(Object entity) {
        Class<?> entityClass = entity.getClass();
        return Stream.concat(Stream.of(entityClass.getDeclaredFields()), Stream.of(entityClass.getFields()))
                .distinct()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .map(field -> getField(field, entity))
                .map(Object::toString)
                .orElse("undefined");
    }

    private  Object getField(Field field, Object entity) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            //should never happen
            throw new RuntimeException();
        }
    }

    private String getAuditUserName() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .flatMap(context -> Optional.ofNullable(context.getAuthentication()))
                .flatMap(authentication -> Optional.ofNullable(authentication.getName()))
                .orElse("undefined");
    }
}
