package com.softjourn.common.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;

@Configuration
@EnableJpaRepositories("com.softjourn.common.audit")
@EntityScan(basePackages = "com.softjourn.common.audit")
public class AuditConfiguration {

    @Autowired
    private AuditRepository repository;

    @PostConstruct
    private void init() {
        AuditEntityListener.setRepository(repository);
    }
}
