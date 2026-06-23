package com.gio.gestorestados.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadatos de la documentacion OpenAPI/Swagger UI (springdoc).
 * Swagger UI disponible en {@code /api/swagger-ui.html} (segun context-path).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gestorEstadosOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Gestor de Estados API")
                .description("API REST para gestion de Proyectos, Sprints, WorkItems, Tareas y Usuarios. "
                        + "Reemplazo funcional de Azure DevOps.")
                .version("v1")
                .contact(new Contact().name("Gestor de Estados")));
    }
}
