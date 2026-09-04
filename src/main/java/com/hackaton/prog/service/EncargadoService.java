package com.hackaton.prog.service;

import com.hackaton.prog.dto.*;
import com.hackaton.prog.model.*;
import com.hackaton.prog.model.enums.MotivoMovimiento;
import com.hackaton.prog.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EncargadoService {

    private final MovimientoRepository movimientoRepository;
    private final ArticuloRepository articuloRepository;
    private final CentroRepository centroRepository;
    private final CampaniaRepository campaniaRepository;
    private final CentroCampaniaRepository centroCampaniaRepository;
    private final InstitucionReceptoraRepository institucionReceptoraRepository;
    private final MovimientoService movimientoService;
    private final InventarioService inventarioService;

    public EncargadoService(MovimientoRepository movimientoRepository,
                            ArticuloRepository articuloRepository,
                            CentroRepository centroRepository,
                            CampaniaRepository campaniaRepository,
                            CentroCampaniaRepository centroCampaniaRepository,
                            InstitucionReceptoraRepository institucionReceptoraRepository,
                            MovimientoService movimientoService,
                            InventarioService inventarioService) {
        this.movimientoRepository = movimientoRepository;
        this.articuloRepository = articuloRepository;
        this.centroRepository = centroRepository;
        this.campaniaRepository = campaniaRepository;
        this.centroCampaniaRepository = centroCampaniaRepository;
        this.institucionReceptoraRepository = institucionReceptoraRepository;
        this.movimientoService = movimientoService;
        this.inventarioService = inventarioService;
    }

    /**
     * Resuelve la campaña activa asociada al centro o la primera activa del sistema.
     */
    @Transactional(readOnly = true)
    public Campania obtenerCampaniaActivaParaCentro(Integer centroId) {
        List<CentroCampania> asignadas = centroCampaniaRepository.findByCentroIdAndActivoTrue(centroId);
        if (!asignadas.isEmpty() && asignadas.get(0).getCampania().getActivo()) {
            return asignadas.get(0).getCampania();
        }
        return campaniaRepository.findByActivoTrue().stream().findFirst()
                .orElse(null);
    }

    /**
     * Obtiene los catálogos necesarios para los formularios de encargado.html
     */
    @Transactional(readOnly = true)
    public CatalogosEncargadoDTO obtenerCatalogos(Integer centroId) {
        CatalogosEncargadoDTO dto = new CatalogosEncargadoDTO();
        dto.setCentroId(centroId);

        Campania campania = obtenerCampaniaActivaParaCentro(centroId);
        if (campania != null) {
            dto.setCampaniaId(campania.getId());
            dto.setCampaniaNombre(campania.getNombre());
        }

        // Artículos con su stock actual en este centro y campaña
        List<Articulo> articulosDb = articuloRepository.findAll();
        List<ArticuloStockDTO> articulosDto = new ArrayList<>();
        for (Articulo art : articulosDb) {
            BigDecimal stock = BigDecimal.ZERO;
            if (campania != null) {
                stock = inventarioService.obtenerStock(centroId, campania.getId(), art.getId());
            }
            articulosDto.add(new ArticuloStockDTO(
                    art.getId(),
                    art.getNombre(),
                    art.getCategoria().getValorDb(),
                    art.getUnidad().getValorDb(),
                    stock
            ));
        }
        dto.setArticulos(articulosDto);

        // Instituciones Receptoras
        List<InstitucionReceptora> institucionesDb = institucionReceptoraRepository.findAll();
        dto.setInstituciones(institucionesDb.stream()
                .map(i -> new OpcionSimpleDTO(i.getId(), i.getNombre()))
                .collect(Collectors.toList()));

        // Centros Destino (Centros activos excepto el centro propio)
        List<Centro> centrosDb = centroRepository.findByActivoTrue();
        dto.setCentrosDestino(centrosDb.stream()
                .filter(c -> !c.getId().equals(centroId))
                .map(c -> new OpcionSimpleDTO(c.getId(), c.getNombre()))
                .collect(Collectors.toList()));

        return dto;
    }

    /**
     * Obtiene métricas en vivo e historial para el Dashboard del Centro.
     */
    @Transactional(readOnly = true)
    public DashboardCentroDTO obtenerDashboardCentro(Integer centroId) {
        Centro centro = centroRepository.findById(centroId)
                .orElseThrow(() -> new IllegalArgumentException("Centro no encontrado: " + centroId));

        Campania campania = obtenerCampaniaActivaParaCentro(centroId);
        Integer campaniaId = campania != null ? campania.getId() : null;
        String campaniaNombre = campania != null ? campania.getNombre() : "Sin Campaña Activa";
        BigDecimal metaCampania = (campania != null && campania.getMetaUnidades() != null)
                ? campania.getMetaUnidades() : BigDecimal.valueOf(3000);

        BigDecimal stockTotal = movimientoRepository.calcularStockTotalCentro(centroId);
        BigDecimal totalMermasMes = movimientoRepository.calcularTotalMermasCentro(centroId);

        List<Movimiento> ultimosMovimientos = movimientoRepository.findTop10ByCentroIdOrderByIdDesc(centroId);
        List<MovimientoHistorialDTO> historialDto = ultimosMovimientos.stream()
                .map(this::mapearHistorial)
                .collect(Collectors.toList());

        return new DashboardCentroDTO(
                centro.getId(),
                centro.getNombre(),
                campaniaId,
                campaniaNombre,
                stockTotal,
                totalMermasMes,
                metaCampania,
                historialDto
        );
    }

    /**
     * Procesa la operación solicitada por el encargado según su tipo.
     */
    public Object registrarMovimiento(RegistroMovimientoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de movimiento no puede ser vacía.");
        }

        Integer centroId = request.getCentroId();
        Integer usuarioId = request.getUsuarioId();
        Integer articuloId = request.getArticuloId();
        BigDecimal cantidad = request.getCantidad();

        // Si no viene campaniaId, inferir la campaña activa del centro
        Integer campaniaId = request.getCampaniaId();
        if (campaniaId == null) {
            Campania camp = obtenerCampaniaActivaParaCentro(centroId);
            if (camp == null) {
                throw new IllegalArgumentException("No hay una campaña activa configurada para este centro.");
            }
            campaniaId = camp.getId();
        }

        String tipoUpper = (request.getTipo() != null) ? request.getTipo().trim().toUpperCase() : "";

        switch (tipoUpper) {
            case "ENTREGA":
                if (request.getInstitucionId() == null) {
                    throw new IllegalArgumentException("Debe seleccionar una institución receptora.");
                }
                return movimientoService.registrarEntrega(
                        centroId, campaniaId, articuloId, cantidad, usuarioId, request.getInstitucionId()
                );

            case "TRANSFERENCIA":
                if (request.getCentroDestinoId() == null) {
                    throw new IllegalArgumentException("Debe seleccionar un centro destino.");
                }
                return movimientoService.registrarTransferencia(
                        centroId, request.getCentroDestinoId(), campaniaId, articuloId, cantidad, usuarioId
                );

            case "MERMA":
                MotivoMovimiento motivoMerma = parsearMotivo(request.getMotivo());
                return movimientoService.registrarMerma(
                        centroId, campaniaId, articuloId, cantidad, usuarioId, motivoMerma, request.getMotivoDetalle()
                );

            case "AJUSTE":
                boolean esPositivo = Boolean.TRUE.equals(request.getEsPositivo());
                MotivoMovimiento motivoAjuste = parsearMotivo(request.getMotivo());
                String detalleAjuste = (request.getMotivoDetalle() != null && !request.getMotivoDetalle().trim().isEmpty())
                        ? request.getMotivoDetalle() : "Ajuste manual de inventario";
                return movimientoService.registrarAjuste(
                        centroId, campaniaId, articuloId, cantidad, esPositivo, usuarioId, motivoAjuste, detalleAjuste
                );

            default:
                throw new IllegalArgumentException("Tipo de movimiento desconocido: " + request.getTipo());
        }
    }

    private MotivoMovimiento parsearMotivo(String motivoTexto) {
        if (motivoTexto == null || motivoTexto.trim().isEmpty()) {
            return MotivoMovimiento.OTRO;
        }
        try {
            return MotivoMovimiento.valueOf(motivoTexto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return MotivoMovimiento.desdeValorDb(motivoTexto.trim().toLowerCase());
        }
    }

    private MovimientoHistorialDTO mapearHistorial(Movimiento m) {
        String tipoStr = (m.getTipo() != null) ? m.getTipo().getValorDb() : "Movimiento";
        String artNom = (m.getArticulo() != null) ? m.getArticulo().getNombre() : "Artículo";
        String unidad = (m.getArticulo() != null && m.getArticulo().getUnidad() != null)
                ? m.getArticulo().getUnidad().getValorDb() : "";
        String autor = (m.getUsuario() != null) ? m.getUsuario().getNombre() : "Usuario";

        String detalle = "";
        if (m.getInstitucionReceptora() != null) {
            detalle = "Hacia " + m.getInstitucionReceptora().getNombre();
        } else if (m.getTransferencia() != null && m.getTransferencia().getCentroDestino() != null) {
            detalle = "Hacia " + m.getTransferencia().getCentroDestino().getNombre();
        } else if (m.getMotivo() != null) {
            detalle = "Motivo: " + m.getMotivo().getValorDb();
            if (m.getMotivoDetalle() != null && !m.getMotivoDetalle().isEmpty()) {
                detalle += " (" + m.getMotivoDetalle() + ")";
            }
        } else if (m.getDonante() != null) {
            String nom = m.getDonante().getNombre();
            detalle = (nom == null || nom.trim().isEmpty() || nom.equalsIgnoreCase("anonimo"))
                    ? "Donante Anónimo" : "Donante: " + nom;
        }

        return new MovimientoHistorialDTO(
                m.getId(),
                tipoStr,
                artNom,
                unidad,
                m.getCantidad(),
                detalle,
                autor,
                m.getFecha()
        );
    }
}
