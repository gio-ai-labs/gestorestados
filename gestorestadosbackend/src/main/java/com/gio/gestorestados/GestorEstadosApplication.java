package com.gio.gestorestados;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Punto de entrada de la aplicacion Gestor de Estados.
 *
 * <p>Empaquetada como {@code .war} (despliegue en contenedor externo, p. ej. WildFly/Tomcat)
 * extendiendo {@link SpringBootServletInitializer}. Sigue siendo ejecutable como standalone
 * via {@code java -jar} o {@code mvn spring-boot:run} (Tomcat embebido en scope provided).
 */
@SpringBootApplication
public class GestorEstadosApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(GestorEstadosApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(GestorEstadosApplication.class, args);
    }
}
