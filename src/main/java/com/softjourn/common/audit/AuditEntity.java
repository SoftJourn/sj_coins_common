package com.softjourn.common.audit;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @Column(columnDefinition = "TEXT")
  private String updatedValue;

  public enum Action {
    CREATE, REMOVE, UPDATE
  }
}
