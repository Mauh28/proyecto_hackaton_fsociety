package com.hackaton.prog.repository;

import com.hackaton.prog.model.CentroCampania;
import com.hackaton.prog.model.CentroCampaniaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CentroCampaniaRepository extends JpaRepository<CentroCampania, CentroCampaniaId> {

    @Query("SELECT cc FROM CentroCampania cc WHERE cc.centro.id = :centroId AND cc.activo = true")
    List<CentroCampania> findByCentroIdAndActivoTrue(@Param("centroId") Integer centroId);

    @Query("SELECT cc FROM CentroCampania cc WHERE cc.campania.id = :campaniaId AND cc.activo = true")
    List<CentroCampania> findByCampaniaIdAndActivoTrue(@Param("campaniaId") Integer campaniaId);
}
