package com.gio.gestorestados.tarea.entity;

import com.gio.gestorestados.shared.audit.BaseEntity;
import com.gio.gestorestados.shared.domain.Estado;
import com.gio.gestorestados.usuario.entity.Usuario;
import com.gio.gestorestados.workitem.entity.WorkItem;
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
 * Tarea bajo un WorkItem. Mapea a {@code gestorestados.tarea}.
 * {@code horas} es CAPTURADA (esfuerzo humano) y alimenta el rollup hacia arriba (T8).
 * {@code orden} soporta el reordenamiento dentro de una columna del tablero (T16).
 */
@Entity
@Table(name = "tarea")
@Getter
@Setter
@NoArgsConstructor
public class Tarea extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workitem_id", nullable = false)
    private WorkItem workitem;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "criterios_aceptacion")
    private String criteriosAceptacion;

    /** Capturada (esfuerzo humano). Alimenta el rollup workitem -&gt; sprint (T8). */
    @Column(name = "horas", nullable = false)
    private BigDecimal horas = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private Estado estado = Estado.PENDIENTE;

    @Column(name = "orden", nullable = false)
    private Integer orden = 0;
}
