package com.gio.gestorestados.tarea.entity;

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

import java.time.OffsetDateTime;

/**
 * Registro de auditoria de cada cambio de estado de una Tarea. Habilita el cycle time (T9).
 * Mapea a {@code gestorestados.tarea_estado_historial}.
 * {@code estadoAnterior} es null en el registro inicial (creacion de la tarea).
 */
@Entity
@Table(name = "tarea_estado_historial")
@Getter
@Setter
@NoArgsConstructor
public class TareaEstadoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tarea_id", nullable = false)
    private Tarea tarea;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 20)
    private Estado estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 20)
    private Estado estadoNuevo;

    @Column(name = "fecha_cambio", nullable = false)
    private OffsetDateTime fechaCambio = OffsetDateTime.now();

    public TareaEstadoHistorial(Tarea tarea, Estado estadoAnterior, Estado estadoNuevo) {
        this.tarea = tarea;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaCambio = OffsetDateTime.now();
    }
}
