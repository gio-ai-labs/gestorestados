package com.gio.gestorestados.shared.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envoltura estable para respuestas paginadas. Se usa en lugar de exponer
 * directamente {@link org.springframework.data.domain.Page} / {@code PageImpl},
 * cuya serializacion JSON no es estable entre versiones (genera el warning
 * {@code Serializing PageImpl instances as-is is not supported}).
 *
 * <p>Forma JSON estable y reutilizable por todos los listados paginados:
 * {@code content} + metadatos de paginacion.
 *
 * @param content       elementos de la pagina actual
 * @param totalElements total de elementos en todas las paginas
 * @param totalPages    total de paginas
 * @param page          indice de la pagina actual (base 0)
 * @param size          tamano de pagina solicitado
 * @param <T>           tipo del elemento de contenido
 */
public record PaginaResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int page,
        int size
) {

    /** Construye la envoltura a partir de un {@link Page} de Spring Data. */
    public static <T> PaginaResponse<T> de(Page<T> pagina) {
        return new PaginaResponse<>(
                pagina.getContent(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.getNumber(),
                pagina.getSize()
        );
    }
}
