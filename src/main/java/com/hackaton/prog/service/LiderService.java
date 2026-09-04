package com.hackaton.prog.service;

import com.hackaton.prog.dto.ActualizarCampaniaLiderRequest;
import com.hackaton.prog.dto.CentroAporteCampaniaDTO;
import com.hackaton.prog.dto.DashboardLiderDTO;
import com.hackaton.prog.dto.OpcionSimpleDTO;
import com.hackaton.prog.exception.AccesoDenegadoException;
import com.hackaton.prog.exception.CuentaInactivaException;
import com.hackaton.prog.exception.UsuarioNoEncontradoException;
import com.hackaton.prog.model.Campania;
import com.hackaton.prog.model.Centro;
import com.hackaton.prog.model.CentroCampania;
import com.hackaton.prog.model.CentroCampaniaId;
import com.hackaton.prog.model.Usuario;
import com.hackaton.prog.model.enums.RolUsuario;
import com.hackaton.prog.repository.CampaniaRepository;
import com.hackaton.prog.repository.CentroCampaniaRepository;
import com.hackaton.prog.repository.CentroRepository;
import com.hackaton.prog.repository.MovimientoRepository;
import com.hackaton.prog.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class LiderService {

    private final CampaniaRepository campaniaRepository;
    private final CentroCampaniaRepository centroCampaniaRepository;
    private final CentroRepository centroRepository;
    private final MovimientoRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;

    public LiderService(CampaniaRepository campaniaRepository,
                         CentroCampaniaRepository centroCampaniaRepository,
                         CentroRepository centroRepository,
                         MovimientoRepository movimientoRepository,
                         UsuarioRepository usuarioRepository) {
        this.campaniaRepository = campaniaRepository;
        this.centroCampaniaRepository = centroCampaniaRepository;
        this.centroRepository = centroRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Valida permisos y obtiene el dashboard analítico de la campaña.
     */
    @Transactional(readOnly = true)
    public DashboardLiderDTO obtenerDashboard(Integer usuarioId, String email, Integer campaniaId) {
        Integer resolvedId = resolverUsuarioId(usuarioId, email);
        Campania campania = validarYObtenerCampania(resolvedId, campaniaId);

        BigDecimal stockActual = movimientoRepository.calcularStockCampania(campania.getId());
        BigDecimal totalRecibido = movimientoRepository.calcularTotalRecibidoCampania(campania.getId());
        BigDecimal totalEntregado = movimientoRepository.calcularTotalEntregadoCampania(campania.getId());
        BigDecimal totalMermas = movimientoRepository.calcularTotalMermasCampania(campania.getId());

        if (stockActual == null) stockActual = BigDecimal.ZERO;
        if (totalRecibido == null) totalRecibido = BigDecimal.ZERO;
        if (totalEntregado == null) totalEntregado = BigDecimal.ZERO;
        if (totalMermas == null) totalMermas = BigDecimal.ZERO;

        BigDecimal porcentajeAvance = BigDecimal.ZERO;
        if (campania.getMetaUnidades() != null && campania.getMetaUnidades().compareTo(BigDecimal.ZERO) > 0) {
            porcentajeAvance = stockActual.divide(campania.getMetaUnidades(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Obtener centros participantes activos
        List<CentroCampania> asociaciones = centroCampaniaRepository.findByCampaniaIdAndActivoTrue(campania.getId());
        List<CentroAporteCampaniaDTO> centrosParticipantes = new ArrayList<>();
        Set<Integer> idsParticipantes = asociaciones.stream()
                .map(cc -> cc.getCentro().getId())
                .collect(Collectors.toSet());

        for (CentroCampania cc : asociaciones) {
            Centro centro = cc.getCentro();
            BigDecimal stockCentro = movimientoRepository.calcularStockCentroCampania(centro.getId(), campania.getId());
            if (stockCentro == null) stockCentro = BigDecimal.ZERO;

            centrosParticipantes.add(new CentroAporteCampaniaDTO(
                    centro.getId(),
                    centro.getNombre(),
                    centro.getInstitucion(),
                    centro.getUbicacion(),
                    stockCentro,
                    cc.getActivo()
            ));
        }

        // Obtener centros disponibles para vincular (activos y no participantes aún)
        List<Centro> todosCentrosActivos = centroRepository.findByActivoTrue();
        List<OpcionSimpleDTO> centrosDisponibles = todosCentrosActivos.stream()
                .filter(c -> !idsParticipantes.contains(c.getId()))
                .map(c -> new OpcionSimpleDTO(c.getId(), c.getNombre() + " (" + c.getInstitucion() + ")"))
                .collect(Collectors.toList());

        return new DashboardLiderDTO(
                campania.getId(),
                campania.getNombre(),
                campania.getDescripcion(),
                campania.getFechaInicio(),
                campania.getFechaFin(),
                campania.getMetaUnidades(),
                campania.getActivo(),
                stockActual,
                totalRecibido,
                totalEntregado,
                totalMermas,
                porcentajeAvance,
                centrosParticipantes,
                centrosDisponibles
        );
    }

    public DashboardLiderDTO obtenerDashboard(Integer usuarioId, Integer campaniaId) {
        return obtenerDashboard(usuarioId, null, campaniaId);
    }

    /**
     * Actualiza la meta, fecha de fin y descripción de la campaña asignada.
     */
    public DashboardLiderDTO actualizarCampania(Integer usuarioId, String email, Integer campaniaId, ActualizarCampaniaLiderRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Los datos de actualización no pueden estar vacíos.");
        }

        Integer resolvedId = resolverUsuarioId(usuarioId, email);
        Campania campania = validarYObtenerCampania(resolvedId, campaniaId);

        if (req.getMetaUnidades() != null) {
            if (req.getMetaUnidades().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("La meta de unidades recolectadas no puede ser negativa.");
            }
            campania.setMetaUnidades(req.getMetaUnidades());
        }

        if (req.getDescripcion() != null) {
            campania.setDescripcion(req.getDescripcion().trim());
        }

        if (req.getFechaFin() != null) {
            if (campania.getFechaInicio() != null && req.getFechaFin().isBefore(campania.getFechaInicio())) {
                throw new IllegalArgumentException("La fecha estimada de cierre no puede ser anterior a la fecha de inicio (" + campania.getFechaInicio() + ").");
            }
            campania.setFechaFin(req.getFechaFin());
        }

        campaniaRepository.save(campania);
        return obtenerDashboard(resolvedId, null, campania.getId());
    }

    public DashboardLiderDTO actualizarCampania(Integer usuarioId, Integer campaniaId, ActualizarCampaniaLiderRequest req) {
        return actualizarCampania(usuarioId, null, campaniaId, req);
    }

    /**
     * Asocia un centro de acopio existente a la campaña del líder.
     */
    public DashboardLiderDTO asociarCentro(Integer usuarioId, String email, Integer campaniaId, Integer centroId) {
        if (centroId == null) {
            throw new IllegalArgumentException("El identificador del centro es obligatorio.");
        }

        Integer resolvedId = resolverUsuarioId(usuarioId, email);
        Campania campania = validarYObtenerCampania(resolvedId, campaniaId);

        Centro centro = centroRepository.findById(centroId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el centro de acopio con ID: " + centroId));

        if (!Boolean.TRUE.equals(centro.getActivo())) {
            throw new IllegalArgumentException("No se puede asociar el centro '" + centro.getNombre() + "' porque se encuentra inactivo.");
        }

        CentroCampaniaId id = new CentroCampaniaId(centro.getId(), campania.getId());
        Optional<CentroCampania> ccOpt = centroCampaniaRepository.findById(id);

        if (ccOpt.isPresent()) {
            CentroCampania cc = ccOpt.get();
            cc.setActivo(true);
            centroCampaniaRepository.save(cc);
        } else {
            CentroCampania nuevo = new CentroCampania(centro, campania, true);
            centroCampaniaRepository.save(nuevo);
        }

        return obtenerDashboard(resolvedId, null, campania.getId());
    }

    public DashboardLiderDTO asociarCentro(Integer usuarioId, Integer campaniaId, Integer centroId) {
        return asociarCentro(usuarioId, null, campaniaId, centroId);
    }

    /**
     * Desasocia un centro de acopio de la campaña del líder (soft-delete activo=false).
     */
    public DashboardLiderDTO desasociarCentro(Integer usuarioId, String email, Integer campaniaId, Integer centroId) {
        if (centroId == null) {
            throw new IllegalArgumentException("El identificador del centro es obligatorio.");
        }

        Integer resolvedId = resolverUsuarioId(usuarioId, email);
        Campania campania = validarYObtenerCampania(resolvedId, campaniaId);

        CentroCampaniaId id = new CentroCampaniaId(centroId, campania.getId());
        CentroCampania cc = centroCampaniaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El centro no se encuentra actualmente vinculado a esta campaña."));

        cc.setActivo(false);
        centroCampaniaRepository.save(cc);

        return obtenerDashboard(resolvedId, null, campania.getId());
    }

    public DashboardLiderDTO desasociarCentro(Integer usuarioId, Integer campaniaId, Integer centroId) {
        return desasociarCentro(usuarioId, null, campaniaId, centroId);
    }

    /**
     * Resuelve el ID numérico del usuario ya sea desde usuarioId o desde su email.
     */
    private Integer resolverUsuarioId(Integer usuarioId, String email) {
        if (usuarioId != null) {
            return usuarioId;
        }
        if (email != null && !email.trim().isEmpty()) {
            Usuario u = usuarioRepository.findByEmail(email.trim().toLowerCase())
                    .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con email: " + email));
            return u.getId();
        }
        throw new IllegalArgumentException("Debe proporcionar el identificador o correo electrónico del usuario.");
    }

    /**
     * Helper que valida el usuario, su rol y su pertenencia o permiso sobre la campaña.
     */
    private Campania validarYObtenerCampania(Integer usuarioId, Integer campaniaId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El identificador del usuario es obligatorio.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioId));

        if (!usuario.isActivo()) {
            throw new CuentaInactivaException("La cuenta de usuario está desactivada.");
        }

        if (usuario.getRol() == RolUsuario.COORDINADOR) {
            if (campaniaId != null) {
                return campaniaRepository.findById(campaniaId)
                        .orElseThrow(() -> new IllegalArgumentException("Campaña con ID " + campaniaId + " no encontrada."));
            } else {
                return campaniaRepository.findByActivoTrue().stream().findFirst()
                        .orElseGet(() -> campaniaRepository.findAll().stream().findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("No hay campañas registradas en el sistema.")));
            }
        } else if (usuario.getRol() == RolUsuario.LIDER) {
            Campania campania = campaniaRepository.findFirstByLiderIdAndActivoTrue(usuario.getId())
                    .orElseGet(() -> campaniaRepository.findByLiderId(usuario.getId()).stream().findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("El usuario Líder no tiene asignada ninguna campaña actualmente.")));

            if (campaniaId != null && !campania.getId().equals(campaniaId)) {
                throw new AccesoDenegadoException("Acceso restringido: No tiene permisos para gestionar una campaña distinta a la suya.");
            }

            return campania;
        } else {
            throw new AccesoDenegadoException("Acceso denegado: El rol " + usuario.getRol() + " no tiene permisos de Líder de Campaña.");
        }
    }
}
