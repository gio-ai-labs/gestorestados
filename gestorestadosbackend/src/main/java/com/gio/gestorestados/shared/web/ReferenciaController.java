package com.gio.gestorestados.shared.web;

import com.gio.gestorestados.shared.domain.Estado;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Endpoints de referencia para alimentar selects de la UI (§6.7).
 */
@RestController
@RequestMapping("/api/v1")
public class ReferenciaController {

    /** Lista de estados disponibles para Sprint/WorkItem/Tarea. */
    @GetMapping("/estados")
    public List<Estado> estados() {
        return Arrays.asList(Estado.values());
    }
}
