
package com.educatoon.backend.utils;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.sql.Timestamp;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author Diego
 */

@MappedSuperclass
@Data
public abstract class AuditBase {
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
}
