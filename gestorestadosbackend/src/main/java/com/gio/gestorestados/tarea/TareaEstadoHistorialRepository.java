package com.gio.gestorestados.tarea;

import com.gio.gestorestados.tarea.entity.TareaEstadoHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TareaEstadoHistorialRepository extends JpaRepository<TareaEstadoHistorial, Long> {

    /** Historial cronologico de una tarea (para GET /historial y cycle time T9). */
    List<TareaEstadoHistorial> findAllByTareaIdOrderByFechaCambioAscIdAsc(Long tareaId);
}
