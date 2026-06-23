package com.gio.gestorestados.proyecto;

import com.gio.gestorestados.proyecto.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    List<Proyecto> findAllByOrderByNombreAsc();

    /** Resolucion por nombre exacto (usado por el adaptador del MCP, §7). */
    Optional<Proyecto> findFirstByNombreIgnoreCase(String nombre);

    /** Filtro por nombre (coincidencia, ignore case) para {@code GET /proyectos?nombre=}. */
    List<Proyecto> findAllByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);
}
