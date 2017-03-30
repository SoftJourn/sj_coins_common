package com.softjourn.common.audit;

import org.springframework.data.repository.CrudRepository;

public interface AuditRepository extends CrudRepository<AuditEntity, Long> {
}
