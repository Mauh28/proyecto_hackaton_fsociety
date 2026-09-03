package com.hackaton.prog.service;

import com.hackaton.prog.model.Centro;
import com.hackaton.prog.repository.CentroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CentroService {

    private final CentroRepository centroRepository;

    public CentroService(CentroRepository centroRepository) {
        this.centroRepository = centroRepository;
    }

    @Transactional(readOnly = true)
    public List<Centro> listarTodos() {
        return centroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Centro> listarActivos() {
        return centroRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Centro> buscarPorId(Integer id) {
        return centroRepository.findById(id);
    }

    public Centro guardar(Centro centro) {
        return centroRepository.save(centro);
    }
}
