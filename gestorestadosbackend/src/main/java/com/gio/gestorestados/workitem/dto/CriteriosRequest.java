package com.gio.gestorestados.workitem.dto;

/**
 * DTO para {@code PATCH /workitems/{id}/criterios} (mapea a {@code update_acceptance_criteria} del MCP, §7).
 *
 * @param criteriosAceptacion nuevos criterios de aceptacion (puede ser vacio para limpiarlos)
 */
public record CriteriosRequest(
        String criteriosAceptacion
) {
}
