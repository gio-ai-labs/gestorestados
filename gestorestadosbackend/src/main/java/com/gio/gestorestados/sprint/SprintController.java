package com.gio.gestorestados.sprint;

import com.gio.gestorestados.shared.domain.Estado;
import com.gio.gestorestados.shared.dto.CambioEstadoRequest;
import com.gio.gestorestados.shared.dto.PaginaResponse;
import com.gio.gestorestados.sprint.dto.SprintRequest;
import com.gio.gestorestados.sprint.dto.SprintResponse;
import com.gio.gestorestados.sprint.dto.SprintTableroResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Endpoints REST de Sprints (§6.7). Listado/creacion bajo proyecto; resto por id de sprint.
 */
@RestController
public class SprintController {

    private final SprintService service;

    public SprintController(SprintService service) {
        this.service = service;
    }

    /**
     * Listado paginado de sprints de un proyecto (T26). Acepta {@code nombre} (filtro por
     * coincidencia), {@code estado} (filtro exacto) y los parametros estandar de paginacion
     * de Spring ({@code page}, {@code size}, {@code sort}). Orden por defecto estable: nombre asc.
     * Pagina fuera de rango -&gt; pagina vacia (sin error).
     */
    @GetMapping("/api/v1/proyectos/{proyectoId}/sprints")
    public PaginaResponse<SprintResponse> listar(
            @PathVariable Long proyectoId,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "estado", required = false) Estado estado,
            @PageableDefault(size = 20, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.listarPorProyecto(proyectoId, nombre, estado, pageable);
    }

    @GetMapping("/api/v1/sprints/{id}")
    public SprintResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    /**
     * Tablero del sprint paginado a nivel de workitem (T27): cada workitem de la pagina trae
     * TODAS sus tareas (las tareas no se paginan). Acepta filtro opcional por {@code estado}
     * del workitem (literal exacto de {@link Estado}) y los parametros estandar de paginacion
     * de Spring ({@code page}, {@code size}, {@code sort}); orden por defecto estable: nombre asc,
     * consistente con el tablero original (T13). Pagina fuera de rango -&gt; pagina vacia (sin error).
     */
    @GetMapping("/api/v1/sprints/{sprintId}/tablero")
    public PaginaResponse<SprintTableroResponse> tablero(
            @PathVariable Long sprintId,
            @RequestParam(name = "estado", required = false) Estado estado,
            @PageableDefault(size = 20, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.obtenerTablero(sprintId, estado, pageable);
    }

    @PostMapping("/api/v1/proyectos/{proyectoId}/sprints")
    public ResponseEntity<SprintResponse> crear(@PathVariable Long proyectoId,
                                                @Valid @RequestBody SprintRequest req,
                                                UriComponentsBuilder uri) {
        SprintResponse creado = service.crear(proyectoId, req);
        URI location = uri.path("/api/v1/sprints/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/api/v1/sprints/{id}")
    public SprintResponse actualizar(@PathVariable Long id, @Valid @RequestBody SprintRequest req) {
        return service.actualizar(id, req);
    }

    @PatchMapping("/api/v1/sprints/{id}/estado")
    public SprintResponse cambiarEstado(@PathVariable Long id,
                                       @Valid @RequestBody CambioEstadoRequest req) {
        return service.cambiarEstado(id, req.estado());
    }

    @DeleteMapping("/api/v1/sprints/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
