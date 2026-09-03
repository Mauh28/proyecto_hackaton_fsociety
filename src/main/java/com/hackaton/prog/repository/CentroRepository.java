package com.hackaton.prog.repository;

import com.hackaton.prog.model.Centro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CentroRepository extends JpaRepository<Centro, Integer> {
    List<Centro> findByActivoTrue();
}
