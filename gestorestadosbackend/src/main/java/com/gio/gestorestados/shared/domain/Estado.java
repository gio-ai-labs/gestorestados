package com.gio.gestorestados.shared.domain;

/**
 * Estado generico unificado para Sprint, WorkItem y Tarea (§2 del plan).
 * Persistido como STRING (ver mapeo {@code @Enumerated(EnumType.STRING)} en las entidades);
 * coincide con el CHECK de la BD: {@code ('PENDIENTE','EN_PROCESO','COMPLETADO')}.
 */
public enum Estado {
    PENDIENTE,
    EN_PROCESO,
    COMPLETADO
}
