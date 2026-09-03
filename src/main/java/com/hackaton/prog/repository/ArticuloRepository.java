package com.hackaton.prog.repository;

import com.hackaton.prog.model.Articulo;
import com.hackaton.prog.model.enums.CategoriaArticulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Integer> {
    List<Articulo> findByCategoria(CategoriaArticulo categoria);
}
