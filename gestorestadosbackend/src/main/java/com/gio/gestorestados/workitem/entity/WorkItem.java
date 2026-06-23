package com.gio.gestorestados.workitem.entity;

import com.gio.gestorestados.shared.audit.BaseEntity;
import com.gio.gestorestados.shared.domain.Estado;
import com.gio.gestorestados.sprint.entity.Sprint;
import com.gio.gestorestados.usuario.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * WorkItem (Requerimiento) bajo un Sprint. Mapea a {@code gestorestados.workitem}.
 * {@code horas} es DERIVADA (rollup = &Sigma; horas de sus tareas): solo lectura via API (T8).
 * {@code usuario} (asignado) es opcional; FK {@code ON DELETE SET NULL}.
 */
@Entity
@Table(name = "workitem")
@Getter
@Setter
@NoArgsConstructor
public class WorkItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sprint_id", nullable = false)
    private Sprint sprint;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "criterios_aceptacion")
    private String criteriosAceptacion;

    /** Derivada (rollup). Solo lectura via API; la mantiene el backend. */
    @Column(name = "horas", nullable = false)
    private BigDecimal horas = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado = Estado.PENDIENTE;
}
