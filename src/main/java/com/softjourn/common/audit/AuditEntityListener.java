package com.softjourn.common.audit;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;
import javax.persistence.Id;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditEntityListener {

  private static AuditRepository repository;
  private static boolean saveValue = false;

  public static void setRepository(AuditRepository repository) {
    AuditEntityListener.repository = repository;
  }

  static void saveValue(boolean save) {
    saveValue = save;
  }

  @PrePersist
  public void postCreate(Object entity) {
    AuditEntity auditEntity = getAuditEntity(entity, AuditEntity.Action.CREATE);
    if (saveValue) auditEntity.setUpdatedValue(entity.toString());
    Optional.ofNullable(repository).ifPresent(repo -> repo.save(auditEntity));
  }

  @PostUpdate
  public void postUpdate(Object entity) {
    AuditEntity auditEntity = getAuditEntity(entity, AuditEntity.Action.UPDATE);
    if (saveValue) auditEntity.setUpdatedValue(entity.toString());
    Optional.ofNullable(repository).ifPresent(repo -> repo.save(auditEntity));
  }

  @PostRemove
  public void postRemove(Object entity) {
    AuditEntity auditEntity = getAuditEntity(entity, AuditEntity.Action.REMOVE);
    Optional.ofNullable(repository).ifPresent(repo -> repo.save(auditEntity));
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
    return Stream
        .concat(Stream.of(entityClass.getDeclaredFields()), Stream.of(entityClass.getFields()))
        .distinct()
        .filter(field -> field.isAnnotationPresent(Id.class))
        .findAny()
        .map(field -> getField(field, entity))
        .map(Object::toString)
        .orElse("undefined");
  }

  private Object getField(Field field, Object entity) {
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
