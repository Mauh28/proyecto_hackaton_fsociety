package com.hackaton.prog.repository;

import com.hackaton.prog.model.Campania;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampaniaRepository extends JpaRepository<Campania, Integer> {
    List<Campania> findByActivoTrue();
    Optional<Campania> findFirstByLiderIdAndActivoTrue(Integer liderId);
    List<Campania> findByLiderId(Integer liderId);
}
