package com.gio.gestorestados.usuario.entity;

import com.gio.gestorestados.shared.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Usuario del catalogo (nombre, email, activo). Sin login: se asigna por seleccion
 * a WorkItems y Tareas. Mapea a {@code gestorestados.usuario}.
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;
}
