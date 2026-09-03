package com.hackaton.prog.repository;

import com.hackaton.prog.model.Donante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonanteRepository extends JpaRepository<Donante, Integer> {
}
