package com.hackaton.prog.repository;

import com.hackaton.prog.model.InstitucionReceptora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitucionReceptoraRepository extends JpaRepository<InstitucionReceptora, Integer> {
}
