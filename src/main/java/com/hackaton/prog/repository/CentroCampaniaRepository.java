package com.hackaton.prog.repository;

import com.hackaton.prog.model.CentroCampania;
import com.hackaton.prog.model.CentroCampaniaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CentroCampaniaRepository extends JpaRepository<CentroCampania, CentroCampaniaId> {
    List<CentroCampania> findByCentroIdAndActivoTrue(Integer centroId);
    List<CentroCampania> findByCampaniaIdAndActivoTrue(Integer campaniaId);
}
