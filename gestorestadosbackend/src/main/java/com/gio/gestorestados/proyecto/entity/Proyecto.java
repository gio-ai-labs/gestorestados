package com.gio.gestorestados.proyecto.entity;

import com.gio.gestorestados.shared.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Proyecto: raiz del arbol Proyecto &rarr; Sprint &rarr; WorkItem &rarr; Tarea.
 * Mapea a {@code gestorestados.proyecto}. El borrado en cascada hacia abajo lo
 * resuelve la BD (FKs {@code ON DELETE CASCADE} definidas en T1).
 */
@Entity
@Table(name = "proyecto")
@Getter
@Setter
@NoArgsConstructor
public class Proyecto extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;
}
