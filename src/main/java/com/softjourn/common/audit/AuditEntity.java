package com.softjourn.common.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Action action;

    private String userName;

    private Instant date;

    private String entityName;

    private String entityId;

    public enum Action {
        CREATE, REMOVE, UPDATE
    }

}
