package com.hackaton.prog.repository;

import com.hackaton.prog.model.Transferencia;
import com.hackaton.prog.model.enums.EstadoTransferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Integer> {
    List<Transferencia> findByCentroOrigenId(Integer centroOrigenId);
    List<Transferencia> findByCentroDestinoId(Integer centroDestinoId);
    List<Transferencia> findByCampaniaId(Integer campaniaId);
    List<Transferencia> findByEstado(EstadoTransferencia estado);
}
