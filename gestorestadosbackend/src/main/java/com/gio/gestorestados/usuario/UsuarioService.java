package com.gio.gestorestados.usuario;

import com.gio.gestorestados.shared.exception.ConflictoException;
import com.gio.gestorestados.shared.exception.RecursoNoEncontradoException;
import com.gio.gestorestados.usuario.dto.UsuarioRequest;
import com.gio.gestorestados.usuario.dto.UsuarioResponse;
import com.gio.gestorestados.usuario.entity.Usuario;
import com.gio.gestorestados.usuario.mapper.UsuarioMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio del catalogo de usuarios. Transacciones cortas:
 * escritura con {@code @Transactional}, lectura con {@code readOnly = true}.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;

    public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(boolean soloActivos) {
        List<Usuario> usuarios = soloActivos
                ? repository.findAllByActivoTrueOrderByNombreAsc()
                : repository.findAllByOrderByNombreAsc();
        return usuarios.stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return mapper.toResponse(buscar(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest req) {
        if (repository.existsByEmailIgnoreCase(req.email())) {
            throw new ConflictoException("Ya existe un usuario con el email " + req.email());
        }
        Usuario creado = repository.save(mapper.toEntity(req));
        return mapper.toResponse(creado);
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest req) {
        Usuario usuario = buscar(id);
        if (repository.existsByEmailIgnoreCaseAndIdNot(req.email(), id)) {
            throw new ConflictoException("Ya existe un usuario con el email " + req.email());
        }
        mapper.updateEntity(usuario, req);
        return mapper.toResponse(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = buscar(id);
        repository.delete(usuario);
    }

    private Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
    }
}
