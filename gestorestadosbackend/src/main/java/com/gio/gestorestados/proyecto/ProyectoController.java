package com.gio.gestorestados.proyecto;

import com.gio.gestorestados.proyecto.dto.ProyectoRequest;
import com.gio.gestorestados.proyecto.dto.ProyectoResponse;
import com.gio.gestorestados.shared.dto.PaginaResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Endpoints REST de Proyectos (base {@code /api/v1/proyectos}). Ver §6.7 del plan.
 */
@RestController
@RequestMapping("/api/v1/proyectos")
public class ProyectoController {

    private final ProyectoService service;

    public ProyectoController(ProyectoService service) {
        this.service = service;
    }

    /**
     * Listado paginado de proyectos (T26). Acepta {@code nombre} (filtro por coincidencia)
     * y los parametros estandar de paginacion de Spring ({@code page}, {@code size}, {@code sort}).
     * Orden por defecto estable: nombre asc. Pagina fuera de rango -&gt; pagina vacia (sin error).
     */
    @GetMapping
    public PaginaResponse<ProyectoResponse> listar(
            @RequestParam(name = "nombre", required = false) String nombre,
            @PageableDefault(size = 20, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.listar(nombre, pageable);
    }

    @GetMapping("/{id}")
    public ProyectoResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<ProyectoResponse> crear(@Valid @RequestBody ProyectoRequest req,
                                                  UriComponentsBuilder uri) {
        ProyectoResponse creado = service.crear(req);
        URI location = uri.path("/api/v1/proyectos/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ProyectoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProyectoRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
