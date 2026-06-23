package com.gio.gestorestados;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Smoke test del scaffold. No carga el contexto de Spring (no requiere BD en build);
 * los tests de integracion con datasource se agregan en tareas posteriores.
 */
class GestorEstadosApplicationTests {

    @Test
    void appClassExiste() {
        assertNotNull(GestorEstadosApplication.class);
    }
}
