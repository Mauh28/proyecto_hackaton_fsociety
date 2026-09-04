package com.hackaton.prog.service;

import com.hackaton.prog.dto.*;
import com.hackaton.prog.model.*;
import com.hackaton.prog.model.enums.RolUsuario;
import com.hackaton.prog.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CoordinadorService {

    private final CampaniaRepository campaniaRepository;
    private final CentroRepository centroRepository;
    private final CentroCampaniaRepository centroCampaniaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoRepository movimientoRepository;

    public CoordinadorService(CampaniaRepository campaniaRepository,
                              CentroRepository centroRepository,
                              CentroCampaniaRepository centroCampaniaRepository,
                              UsuarioRepository usuarioRepository,
                              MovimientoRepository movimientoRepository) {
        this.campaniaRepository = campaniaRepository;
        this.centroRepository = centroRepository;
        this.centroCampaniaRepository = centroCampaniaRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Devuelve las métricas consolidadas del Dashboard Global y la comparativa por centro.
     */
    @Transactional(readOnly = true)
    public DashboardGlobalDTO obtenerDashboardGlobal() {
        BigDecimal stockGlobal = movimientoRepository.calcularStockGlobal();
        BigDecimal mermaTotal = movimientoRepository.calcularMermaGlobal();

        List<Centro> centros = centroRepository.findAll();
        long centrosActivos = centros.stream().filter(c -> Boolean.TRUE.equals(c.getActivo())).count();

        String artMasDonado = movimientoRepository.obtenerNombreArticuloMasDonado();
        if (artMasDonado == null || artMasDonado.isEmpty()) {
            artMasDonado = "Agua embotellada 1L";
        }

        // Obtener la campaña activa principal
        Campania campaniaActiva = campaniaRepository.findByActivoTrue().stream().findFirst().orElse(null);
        String campaniaNombre = campaniaActiva != null ? campaniaActiva.getNombre() : "Sin Campaña Activa";
        BigDecimal metaCampania = (campaniaActiva != null && campaniaActiva.getMetaUnidades() != null)
                ? campaniaActiva.getMetaUnidades() : BigDecimal.valueOf(10000);

        // Construir comparativa de centros con su encargado y stock total
        List<CentroComparativaDTO> comparativa = new ArrayList<>();
        for (Centro centro : centros) {
            BigDecimal stockCentro = movimientoRepository.calcularStockTotalCentro(centro.getId());

            // Buscar encargado asignado a este centro
            List<Usuario> usuariosCentro = usuarioRepository.findByCentroId(centro.getId());
            String nombreEncargado = usuariosCentro.stream()
                    .filter(u -> u.getRol() == RolUsuario.ENCARGADO)
                    .map(Usuario::getNombre)
                    .findFirst()
                    .orElse(centro.getInstitucion());

            comparativa.add(new CentroComparativaDTO(
                    centro.getId(),
                    centro.getNombre(),
                    nombreEncargado,
                    stockCentro
            ));
        }

        return new DashboardGlobalDTO(
                stockGlobal,
                mermaTotal,
                centrosActivos,
                artMasDonado,
                campaniaNombre,
                metaCampania,
                comparativa
        );
    }

    /**
     * Carga los catálogos para los selectores de los formularios de creación.
     */
    @Transactional(readOnly = true)
    public CatalogosCoordinadorDTO obtenerCatalogos() {
        // Usuarios con rol de Encargado
        List<Usuario> encargadosDb = usuarioRepository.findByRol(RolUsuario.ENCARGADO);
        List<OpcionSimpleDTO> encargados = encargadosDb.stream()
                .map(u -> new OpcionSimpleDTO(u.getId(), u.getNombre() + " (" + u.getEmail() + ")"))
                .collect(Collectors.toList());

        // Campañas activas
        List<Campania> campaniasDb = campaniaRepository.findAll();
        List<OpcionSimpleDTO> campanias = campaniasDb.stream()
                .map(c -> new OpcionSimpleDTO(c.getId(), c.getNombre() + (Boolean.TRUE.equals(c.getActivo()) ? " [Activa]" : " [Inactiva]")))
                .collect(Collectors.toList());

        return new CatalogosCoordinadorDTO(encargados, campanias);
    }

    /**
     * Guarda o actualiza una campaña.
     */
    public Campania guardarCampania(GuardarCampaniaRequest req) {
        if (req == null || req.getNombre() == null || req.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la campaña es obligatorio.");
        }
        if (req.getFechaInicio() == null) {
            req.setFechaInicio(LocalDate.now());
        }

        Campania c = new Campania();
        c.setNombre(req.getNombre().trim());
        c.setFechaInicio(req.getFechaInicio());
        c.setFechaFin(req.getFechaFin());
        BigDecimal meta = (req.getMetaUnidades() != null && req.getMetaUnidades().compareTo(BigDecimal.ONE) >= 0)
                ? req.getMetaUnidades().setScale(0, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(5000);
        c.setMetaUnidades(meta);
        c.setActivo(req.getActivo() != null ? req.getActivo() : true);

        return campaniaRepository.save(c);
    }

    /**
     * Registra un nuevo centro de acopio y lo vincula a la campaña y encargado.
     */
    public Centro registrarCentro(GuardarCentroRequest req) {
        if (req == null || req.getNombre() == null || req.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del centro de acopio es obligatorio.");
        }
        if (req.getInstitucion() == null || req.getInstitucion().trim().isEmpty()) {
            throw new IllegalArgumentException("La institución responsable es obligatoria.");
        }

        Centro centro = new Centro();
        centro.setNombre(req.getNombre().trim());
        centro.setInstitucion(req.getInstitucion().trim());
        centro.setUbicacion(req.getUbicacion() != null ? req.getUbicacion().trim() : "Sede Central");
        centro.setLatitud(req.getLatitud());
        centro.setLongitud(req.getLongitud());
        centro.setActivo(req.getActivo() != null ? req.getActivo() : true);
        centro = centroRepository.save(centro);

        // Si se vinculó a una campaña, registrar en centros_campanias
        if (req.getCampaniaId() != null) {
            Campania campania = campaniaRepository.findById(req.getCampaniaId()).orElse(null);
            if (campania != null) {
                CentroCampania vinculo = new CentroCampania(centro, campania, true);
                centroCampaniaRepository.save(vinculo);
            }
        }

        // Si se asignó un encargado, asociarle el centro
        if (req.getEncargadoId() != null) {
            Usuario encargado = usuarioRepository.findById(req.getEncargadoId()).orElse(null);
            if (encargado != null) {
                encargado.setCentro(centro);
                usuarioRepository.save(encargado);
            }
        }

        return centro;
    }

    /**
     * Retorna los centros activos para pintar en el mapa geográfico interactivo con sus métricas y nivel de suministro.
     */
    @Transactional(readOnly = true)
    public List<CentroMapaDTO> obtenerCentrosMapa() {
        List<Centro> centros = centroRepository.findByActivoTrue();
        List<CentroMapaDTO> resultado = new ArrayList<>();

        for (Centro centro : centros) {
            if (centro.getLatitud() == null || centro.getLongitud() == null) {
                continue;
            }

            BigDecimal stockTotal = movimientoRepository.calcularStockTotalCentro(centro.getId());
            if (stockTotal == null) {
                stockTotal = BigDecimal.ZERO;
            }

            List<Usuario> usuariosCentro = usuarioRepository.findByCentroId(centro.getId());
            String nombreEncargado = usuariosCentro.stream()
                    .filter(u -> u.getRol() == RolUsuario.ENCARGADO)
                    .map(Usuario::getNombre)
                    .findFirst()
                    .orElse(centro.getInstitucion());

            String nivelSuministro;
            if (stockTotal.compareTo(BigDecimal.valueOf(1500)) >= 0) {
                nivelSuministro = "OPTIMO";
            } else if (stockTotal.compareTo(BigDecimal.valueOf(500)) >= 0) {
                nivelSuministro = "NORMAL";
            } else {
                nivelSuministro = "CRITICO";
            }

            resultado.add(new CentroMapaDTO(
                    centro.getId(),
                    centro.getNombre(),
                    centro.getInstitucion(),
                    centro.getUbicacion(),
                    centro.getLatitud(),
                    centro.getLongitud(),
                    stockTotal,
                    nombreEncargado,
                    nivelSuministro,
                    centro.getActivo()
            ));
        }

        return resultado;
    }
}
