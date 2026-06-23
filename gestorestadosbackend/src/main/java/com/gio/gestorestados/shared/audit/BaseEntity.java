package com.gio.gestorestados.shared.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

/**
 * Superclase de auditoria para todas las entidades del dominio.
 *
 * <p>Provee {@code fecha_creacion} y {@code fecha_actualizacion} pobladas automaticamente
 * por JPA auditing (ver {@code shared.config.JpaConfig}). Mapea a columnas {@code TIMESTAMPTZ}
 * con default en BD, pero el control efectivo lo lleva Hibernate via estas anotaciones.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;
}
