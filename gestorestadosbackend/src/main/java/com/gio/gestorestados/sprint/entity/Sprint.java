package com.gio.gestorestados.sprint.entity;

import com.gio.gestorestados.proyecto.entity.Proyecto;
import com.gio.gestorestados.shared.audit.BaseEntity;
import com.gio.gestorestados.shared.domain.Estado;
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
import java.time.LocalDate;

/**
 * Sprint/Version bajo un Proyecto. Mapea a {@code gestorestados.sprint}.
 * {@code horas} es DERIVADA (rollup = &Sigma; horas de sus workitems): solo lectura via API,
 * la recalcula el backend (T8).
 */
@Entity
@Table(name = "sprint")
@Getter
@Setter
@NoArgsConstructor
public class Sprint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado = Estado.PENDIENTE;

    /** Derivada (rollup). Solo lectura via API; la mantiene el backend. */
    @Column(name = "horas", nullable = false)
    private BigDecimal horas = BigDecimal.ZERO;
}
