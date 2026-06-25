package com.gio.gestorestados.proyecto;

import com.gio.gestorestados.proyecto.entity.Proyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    List<Proyecto> findAllByOrderByNombreAsc();

    /** Resolucion por nombre exacto (usado por el adaptador del MCP, §7). */
    Optional<Proyecto> findFirstByNombreIgnoreCase(String nombre);

    /** Filtro por nombre (coincidencia, ignore case) para {@code GET /proyectos?nombre=}. */
    List<Proyecto> findAllByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);

    /** Listado paginado sin filtro para {@code GET /proyectos} (T26). */
    Page<Proyecto> findAll(Pageable pageable);

    /** Listado paginado con filtro por nombre para {@code GET /proyectos?nombre=} (T26). */
    Page<Proyecto> findAllByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
