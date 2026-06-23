package com.gio.gestorestados.workitem;

import com.gio.gestorestados.shared.dto.CambioEstadoRequest;
import com.gio.gestorestados.workitem.dto.CriteriosRequest;
import com.gio.gestorestados.workitem.dto.WorkItemRequest;
import com.gio.gestorestados.workitem.dto.WorkItemResponse;
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
 * Endpoints REST de WorkItems (§6.7). Listado/creacion bajo sprint; resto por id de workitem.
 * {@code GET /workitems/{id}/tareas} y {@code /cycle-time} se implementan en T7/T9.
 */
@RestController
public class WorkItemController {

    private final WorkItemService service;

    public WorkItemController(WorkItemService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/sprints/{sprintId}/workitems")
    public List<WorkItemResponse> listar(@PathVariable Long sprintId) {
        return service.listarPorSprint(sprintId);
    }

    @GetMapping("/api/v1/workitems/{id}")
    public WorkItemResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping("/api/v1/sprints/{sprintId}/workitems")
    public ResponseEntity<WorkItemResponse> crear(@PathVariable Long sprintId,
                                                  @Valid @RequestBody WorkItemRequest req,
                                                  UriComponentsBuilder uri) {
        WorkItemResponse creado = service.crear(sprintId, req);
        URI location = uri.path("/api/v1/workitems/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/api/v1/workitems/{id}")
    public WorkItemResponse actualizar(@PathVariable Long id, @Valid @RequestBody WorkItemRequest req) {
        return service.actualizar(id, req);
    }

    @PatchMapping("/api/v1/workitems/{id}/estado")
    public WorkItemResponse cambiarEstado(@PathVariable Long id,
                                         @Valid @RequestBody CambioEstadoRequest req) {
        return service.cambiarEstado(id, req.estado());
    }

    @PatchMapping("/api/v1/workitems/{id}/criterios")
    public WorkItemResponse actualizarCriterios(@PathVariable Long id,
                                               @RequestBody CriteriosRequest req) {
        return service.actualizarCriterios(id, req.criteriosAceptacion());
    }

    @DeleteMapping("/api/v1/workitems/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
