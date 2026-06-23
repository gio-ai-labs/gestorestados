package com.gio.gestorestados.tarea;

import com.gio.gestorestados.shared.dto.CambioEstadoRequest;
import com.gio.gestorestados.tarea.dto.HistorialResponse;
import com.gio.gestorestados.tarea.dto.TareaRequest;
import com.gio.gestorestados.tarea.dto.TareaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Endpoints REST de Tareas (§6.7). Listado bajo workitem; creacion bajo workitem;
 * resto por id de tarea. Incluye {@code GET /workitems/{id}/tareas} y {@code GET /tareas/{id}/historial}.
 */
@RestController
public class TareaController {

    private final TareaService service;

    public TareaController(TareaService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/workitems/{workitemId}/tareas")
    public List<TareaResponse> listarPorWorkItem(@PathVariable Long workitemId) {
        return service.listarPorWorkItem(workitemId);
    }

    @GetMapping("/api/v1/tareas/{id}")
    public TareaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @GetMapping("/api/v1/tareas/{id}/historial")
    public List<HistorialResponse> historial(@PathVariable Long id) {
        return service.historial(id);
    }

    @PostMapping("/api/v1/workitems/{workitemId}/tareas")
    public ResponseEntity<TareaResponse> crear(@PathVariable Long workitemId,
                                               @Valid @RequestBody TareaRequest req,
                                               UriComponentsBuilder uri) {
        TareaResponse creada = service.crear(workitemId, req);
        URI location = uri.path("/api/v1/tareas/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/api/v1/tareas/{id}")
    public TareaResponse actualizar(@PathVariable Long id, @Valid @RequestBody TareaRequest req) {
        return service.actualizar(id, req);
    }

    @PatchMapping("/api/v1/tareas/{id}/estado")
    public TareaResponse cambiarEstado(@PathVariable Long id,
                                      @Valid @RequestBody CambioEstadoRequest req) {
        return service.cambiarEstado(id, req.estado());
    }

    @DeleteMapping("/api/v1/tareas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
