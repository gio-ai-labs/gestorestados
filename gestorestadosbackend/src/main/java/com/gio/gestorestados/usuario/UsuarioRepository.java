package com.gio.gestorestados.usuario;

import com.gio.gestorestados.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Usuario> findAllByActivoTrueOrderByNombreAsc();

    List<Usuario> findAllByOrderByNombreAsc();
}
