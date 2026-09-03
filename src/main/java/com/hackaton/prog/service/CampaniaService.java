package com.hackaton.prog.service;

import com.hackaton.prog.model.Campania;
import com.hackaton.prog.repository.CampaniaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CampaniaService {

    private final CampaniaRepository campaniaRepository;

    public CampaniaService(CampaniaRepository campaniaRepository) {
        this.campaniaRepository = campaniaRepository;
    }

    @Transactional(readOnly = true)
    public List<Campania> listarTodas() {
        return campaniaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Campania> listarActivas() {
        return campaniaRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Campania> buscarPorId(Integer id) {
        return campaniaRepository.findById(id);
    }

    public Campania guardar(Campania campania) {
        return campaniaRepository.save(campania);
    }
}
