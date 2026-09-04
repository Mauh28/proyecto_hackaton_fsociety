package com.hackaton.prog.repository;

import com.hackaton.prog.model.Usuario;
import com.hackaton.prog.model.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(RolUsuario rol);
    List<Usuario> findByCentroId(Integer centroId);
}
