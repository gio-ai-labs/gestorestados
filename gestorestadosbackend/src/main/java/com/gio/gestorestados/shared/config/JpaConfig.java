package com.gio.gestorestados.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Habilita JPA auditing para poblar {@code fechaCreacion}/{@code fechaActualizacion}
 * de {@link com.gio.gestorestados.shared.audit.BaseEntity}.
 *
 * <p>Se registra un {@link DateTimeProvider} explicito que devuelve {@link OffsetDateTime}:
 * el proveedor por defecto entrega {@code LocalDateTime}, que no se convierte a los campos
 * {@code OffsetDateTime} de la entidad y dejaria las fechas nulas (violacion de NOT NULL).
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaConfig {

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }
}
