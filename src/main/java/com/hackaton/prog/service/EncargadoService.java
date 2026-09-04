package com.hackaton.prog.service;

import com.hackaton.prog.dto.*;
import com.hackaton.prog.model.*;
import com.hackaton.prog.model.enums.MotivoMovimiento;
import com.hackaton.prog.model.enums.TipoMovimiento;
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

    @Transactional(readOnly = true)
    public Campania obtenerCampaniaParaCentro(Integer centroId, Integer campaniaId) {
        if (campaniaId != null) {
            return campaniaRepository.findById(campaniaId).orElseGet(() -> obtenerCampaniaActivaParaCentro(centroId));
        }
        return obtenerCampaniaActivaParaCentro(centroId);
    }

    /**
     * Obtiene los catálogos necesarios para los formularios de encargado.html
     */
    @Transactional(readOnly = true)
    public CatalogosEncargadoDTO obtenerCatalogos(Integer centroId, Integer campaniaId) {
        CatalogosEncargadoDTO dto = new CatalogosEncargadoDTO();
        dto.setCentroId(centroId);

        Campania campania = obtenerCampaniaParaCentro(centroId, campaniaId);
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

        // Centros Destino (Centros activos que participen en la misma campaña, excepto el centro propio)
        List<Centro> centrosDb = centroRepository.findByActivoTrue();
        final Integer activeCampaniaId = (campania != null) ? campania.getId() : null;

        List<OpcionSimpleDTO> centrosDestinoDto = centrosDb.stream()
                .filter(c -> !c.getId().equals(centroId))
                .filter(c -> {
                    if (activeCampaniaId == null) return true;
                    List<CentroCampania> asignados = centroCampaniaRepository.findByCentroIdAndActivoTrue(c.getId());
                    // Si el centro destino tiene asignaciones específicas, debe incluir la campaña activa
                    return asignados.isEmpty() || asignados.stream()
                            .anyMatch(cc -> cc.getCampania().getId().equals(activeCampaniaId));
                })
                .map(c -> new OpcionSimpleDTO(c.getId(), c.getNombre()))
                .collect(Collectors.toList());

        dto.setCentrosDestino(centrosDestinoDto);

        // Campañas disponibles
        List<CentroCampania> asignadasCentro = centroCampaniaRepository.findByCentroIdAndActivoTrue(centroId);
        List<Campania> campaniasDisponiblesList;
        if (!asignadasCentro.isEmpty()) {
            campaniasDisponiblesList = asignadasCentro.stream().map(CentroCampania::getCampania).filter(c -> Boolean.TRUE.equals(c.getActivo())).collect(Collectors.toList());
        } else {
            campaniasDisponiblesList = campaniaRepository.findByActivoTrue();
        }
        if (campaniasDisponiblesList.isEmpty()) {
            campaniasDisponiblesList = campaniaRepository.findAll();
        }

        dto.setCampaniasDisponibles(campaniasDisponiblesList.stream()
                .map(c -> new OpcionSimpleDTO(c.getId(), c.getNombre() + (Boolean.TRUE.equals(c.getActivo()) ? " (Activa)" : " (Inactiva)")))
                .collect(Collectors.toList()));

        dto.setCentrosDisponibles(centrosDb.stream()
                .map(c -> new OpcionSimpleDTO(c.getId(), c.getNombre() + " (" + c.getInstitucion() + ")"))
                .collect(Collectors.toList()));

        return dto;
    }

    public CatalogosEncargadoDTO obtenerCatalogos(Integer centroId) {
        return obtenerCatalogos(centroId, null);
    }

    /**
     * Obtiene métricas en vivo e historial para el Dashboard del Centro.
     */
    @Transactional(readOnly = true)
    public DashboardCentroDTO obtenerDashboardCentro(Integer centroId, Integer campaniaId) {
        Centro centro = centroRepository.findById(centroId)
                .orElseThrow(() -> new IllegalArgumentException("Centro no encontrado: " + centroId));

        Campania campania = obtenerCampaniaParaCentro(centroId, campaniaId);
        Integer resolvedCampaniaId = campania != null ? campania.getId() : null;
        String campaniaNombre = campania != null ? campania.getNombre() : "Sin Campaña Activa";
        BigDecimal metaCampania = (campania != null && campania.getMetaUnidades() != null)
                ? campania.getMetaUnidades() : BigDecimal.valueOf(3000);

        BigDecimal stockTotal = movimientoRepository.calcularStockTotalCentro(centroId);
        BigDecimal totalMermasMes = movimientoRepository.calcularTotalMermasCentro(centroId);

        List<Movimiento> ultimosMovimientos = movimientoRepository.findTop10ByCentroIdOrderByIdDesc(centroId);
        List<MovimientoHistorialDTO> historialDto = ultimosMovimientos.stream()
                .map(this::mapearHistorial)
                .collect(Collectors.toList());

        DashboardCentroDTO dashboardDTO = new DashboardCentroDTO(
                centro.getId(),
                centro.getNombre(),
                resolvedCampaniaId,
                campaniaNombre,
                stockTotal,
                totalMermasMes,
                metaCampania,
                historialDto
        );

        List<CentroCampania> asignadasCentro = centroCampaniaRepository.findByCentroIdAndActivoTrue(centroId);
        List<Campania> campaniasDisponiblesList;
        if (!asignadasCentro.isEmpty()) {
            campaniasDisponiblesList = asignadasCentro.stream().map(CentroCampania::getCampania).filter(c -> Boolean.TRUE.equals(c.getActivo())).collect(Collectors.toList());
        } else {
            campaniasDisponiblesList = campaniaRepository.findByActivoTrue();
        }
        if (campaniasDisponiblesList.isEmpty()) {
            campaniasDisponiblesList = campaniaRepository.findAll();
        }

        dashboardDTO.setCampaniasDisponibles(campaniasDisponiblesList.stream()
                .map(c -> new OpcionSimpleDTO(c.getId(), c.getNombre() + (Boolean.TRUE.equals(c.getActivo()) ? " (Activa)" : " (Inactiva)")))
                .collect(Collectors.toList()));

        dashboardDTO.setCentrosDisponibles(centroRepository.findByActivoTrue().stream()
                .map(c -> new OpcionSimpleDTO(c.getId(), c.getNombre() + " (" + c.getInstitucion() + ")"))
                .collect(Collectors.toList()));

        return dashboardDTO;
    }

    public DashboardCentroDTO obtenerDashboardCentro(Integer centroId) {
        return obtenerDashboardCentro(centroId, null);
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
                boolean esBeneficiario = "beneficiario".equalsIgnoreCase(request.getTipoEntrega()) ||
                        (request.getBeneficiarioNombre() != null && !request.getBeneficiarioNombre().trim().isEmpty());
                if (esBeneficiario) {
                    if (request.getBeneficiarioNombre() == null || request.getBeneficiarioNombre().trim().isEmpty()) {
                        throw new IllegalArgumentException("Debe ingresar el nombre o identificación del beneficiario directo.");
                    }
                    return movimientoService.registrarEntrega(
                            centroId, campaniaId, articuloId, cantidad, usuarioId, null, request.getBeneficiarioNombre().trim()
                    );
                } else {
                    if (request.getInstitucionId() == null) {
                        throw new IllegalArgumentException("Debe seleccionar una institución receptora.");
                    }
                    return movimientoService.registrarEntrega(
                            centroId, campaniaId, articuloId, cantidad, usuarioId, request.getInstitucionId(), null
                    );
                }

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
        } else if (m.getTipo() == TipoMovimiento.ENTREGA && m.getMotivoDetalle() != null && !m.getMotivoDetalle().isEmpty()) {
            detalle = m.getMotivoDetalle();
        } else if (m.getTransferencia() != null && m.getTransferencia().getCentroDestino() != null) {
            detalle = "Hacia " + m.getTransferencia().getCentroDestino().getNombre();
        } else if (m.getMotivo() != null) {
            detalle = "Motivo: " + m.getMotivo().getValorDb();
            if (m.getMotivoDetalle() != null && !m.getMotivoDetalle().isEmpty()) {
                detalle += " (" + m.getMotivoDetalle() + ")";
            }
        } else if (m.getTipo() == TipoMovimiento.RECEPCION) {
            if (m.getDonante() != null && m.getDonante().getNombre() != null && !m.getDonante().getNombre().trim().isEmpty()) {
                String nom = m.getDonante().getNombre().trim();
                if (nom.equalsIgnoreCase("anonimo") || nom.equalsIgnoreCase("anónimo")
                        || nom.equalsIgnoreCase("donante anonimo") || nom.equalsIgnoreCase("donante anónimo")) {
                    detalle = "Donante: Anonimo";
                } else {
                    detalle = "Donante: " + nom;
                }
            } else {
                detalle = "Donante: Anonimo";
            }
        } else if (m.getDonante() != null) {
            String nom = m.getDonante().getNombre();
            detalle = (nom == null || nom.trim().isEmpty() || nom.equalsIgnoreCase("anonimo") || nom.equalsIgnoreCase("anónimo"))
                    ? "Donante: Anonimo" : "Donante: " + nom;
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
