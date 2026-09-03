package com.hackaton.prog.repository;

import com.hackaton.prog.model.Campania;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CampaniaRepository extends JpaRepository<Campania, Integer> {
    List<Campania> findByActivoTrue();
}
