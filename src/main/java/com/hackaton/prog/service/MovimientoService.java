package com.hackaton.prog.service;

import com.hackaton.prog.exception.StockInsuficienteException;
import com.hackaton.prog.model.*;
import com.hackaton.prog.model.enums.EstadoTransferencia;
import com.hackaton.prog.model.enums.MotivoMovimiento;
import com.hackaton.prog.model.enums.TipoMovimiento;
import com.hackaton.prog.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CentroRepository centroRepository;
    private final CampaniaRepository campaniaRepository;
    private final ArticuloRepository articuloRepository;
    private final UsuarioRepository usuarioRepository;
    private final DonanteRepository donanteRepository;
    private final InstitucionReceptoraRepository institucionReceptoraRepository;
    private final TransferenciaRepository transferenciaRepository;
    private final InventarioService inventarioService;

    public MovimientoService(MovimientoRepository movimientoRepository,
                             CentroRepository centroRepository,
                             CampaniaRepository campaniaRepository,
                             ArticuloRepository articuloRepository,
                             UsuarioRepository usuarioRepository,
                             DonanteRepository donanteRepository,
                             InstitucionReceptoraRepository institucionReceptoraRepository,
                             TransferenciaRepository transferenciaRepository,
                             InventarioService inventarioService) {
        this.movimientoRepository = movimientoRepository;
        this.centroRepository = centroRepository;
        this.campaniaRepository = campaniaRepository;
        this.articuloRepository = articuloRepository;
        this.usuarioRepository = usuarioRepository;
        this.donanteRepository = donanteRepository;
        this.institucionReceptoraRepository = institucionReceptoraRepository;
        this.transferenciaRepository = transferenciaRepository;
        this.inventarioService = inventarioService;
    }

    /**
     * Registra una recepción por donación (nominal o anónima).
     */
    public Movimiento registrarRecepcion(Integer centroId, Integer campaniaId, Integer articuloId,
                                         BigDecimal cantidad, Integer usuarioId, String nombreDonante) {
        validarCantidadPositiva(cantidad);

        Centro centro = obtenerCentro(centroId);
        Campania campania = obtenerCampania(campaniaId);
        Articulo articulo = obtenerArticulo(articuloId);
        Usuario usuario = obtenerUsuario(usuarioId);

        Donante donante = null;
        if (nombreDonante != null && !nombreDonante.trim().isEmpty() && !nombreDonante.equalsIgnoreCase("ANONIMO")) {
            donante = donanteRepository.save(new Donante(nombreDonante.trim()));
        }

        Movimiento mov = new Movimiento();
        mov.setTipo(TipoMovimiento.RECEPCION);
        mov.setCentro(centro);
        mov.setCampania(campania);
        mov.setArticulo(articulo);
        mov.setCantidad(cantidad);
        mov.setUsuario(usuario);
        mov.setDonante(donante);

        return movimientoRepository.save(mov);
    }

    /**
     * Registra una entrega / canalización a una institución receptora.
     * Valida estrictamente que haya stock disponible antes de persistir.
     */
    public Movimiento registrarEntrega(Integer centroId, Integer campaniaId, Integer articuloId,
                                       BigDecimal cantidad, Integer usuarioId, Integer institucionId) {
        validarCantidadPositiva(cantidad);
        inventarioService.validarStockSuficiente(centroId, campaniaId, articuloId, cantidad);

        Centro centro = obtenerCentro(centroId);
        Campania campania = obtenerCampania(campaniaId);
        Articulo articulo = obtenerArticulo(articuloId);
        Usuario usuario = obtenerUsuario(usuarioId);
        InstitucionReceptora institucion = institucionReceptoraRepository.findById(institucionId)
                .orElseThrow(() -> new IllegalArgumentException("Institución receptora no encontrada con ID: " + institucionId));

        Movimiento mov = new Movimiento();
        mov.setTipo(TipoMovimiento.ENTREGA);
        mov.setCentro(centro);
        mov.setCampania(campania);
        mov.setArticulo(articulo);
        mov.setCantidad(cantidad);
        mov.setUsuario(usuario);
        mov.setInstitucionReceptora(institucion);

        return movimientoRepository.save(mov);
    }

    /**
     * Registra una merma con motivo obligatorio (caducidad, daño, perdida, etc.).
     * Valida que no exceda el stock disponible.
     */
    public Movimiento registrarMerma(Integer centroId, Integer campaniaId, Integer articuloId,
                                     BigDecimal cantidad, Integer usuarioId, MotivoMovimiento motivo, String detalle) {
        validarCantidadPositiva(cantidad);
        if (motivo == null) {
            throw new IllegalArgumentException("El motivo es estrictamente obligatorio para registrar una merma.");
        }
        inventarioService.validarStockSuficiente(centroId, campaniaId, articuloId, cantidad);

        Centro centro = obtenerCentro(centroId);
        Campania campania = obtenerCampania(campaniaId);
        Articulo articulo = obtenerArticulo(articuloId);
        Usuario usuario = obtenerUsuario(usuarioId);

        Movimiento mov = new Movimiento();
        mov.setTipo(TipoMovimiento.MERMA);
        mov.setCentro(centro);
        mov.setCampania(campania);
        mov.setArticulo(articulo);
        mov.setCantidad(cantidad);
        mov.setUsuario(usuario);
        mov.setMotivo(motivo);
        mov.setMotivoDetalle(detalle);

        return movimientoRepository.save(mov);
    }

    /**
     * Registra una transferencia entre centros.
     * Disminuye el stock en el origen (TRANSFERENCIA_SALIDA) e incrementa en el destino (TRANSFERENCIA_ENTRADA).
     */
    public Transferencia registrarTransferencia(Integer centroOrigenId, Integer centroDestinoId,
                                               Integer campaniaId, Integer articuloId,
                                               BigDecimal cantidad, Integer usuarioId) {
        validarCantidadPositiva(cantidad);
        if (centroOrigenId.equals(centroDestinoId)) {
            throw new IllegalArgumentException("El centro de origen no puede ser el mismo que el centro de destino.");
        }
        inventarioService.validarStockSuficiente(centroOrigenId, campaniaId, articuloId, cantidad);

        Centro centroOrigen = obtenerCentro(centroOrigenId);
        Centro centroDestino = obtenerCentro(centroDestinoId);
        Campania campania = obtenerCampania(campaniaId);
        Articulo articulo = obtenerArticulo(articuloId);
        Usuario usuario = obtenerUsuario(usuarioId);

        // 1. Crear registro de Transferencia
        Transferencia transferencia = new Transferencia(
                centroOrigen, centroDestino, campania, articulo, cantidad, EstadoTransferencia.COMPLETADA
        );
        transferencia = transferenciaRepository.save(transferencia);

        // 2. Movimiento de salida en el centro origen
        Movimiento movSalida = new Movimiento();
        movSalida.setTipo(TipoMovimiento.TRANSFERENCIA_SALIDA);
        movSalida.setCentro(centroOrigen);
        movSalida.setCampania(campania);
        movSalida.setArticulo(articulo);
        movSalida.setCantidad(cantidad);
        movSalida.setUsuario(usuario);
        movSalida.setTransferencia(transferencia);
        movimientoRepository.save(movSalida);

        // 3. Movimiento de entrada en el centro destino
        Movimiento movEntrada = new Movimiento();
        movEntrada.setTipo(TipoMovimiento.TRANSFERENCIA_ENTRADA);
        movEntrada.setCentro(centroDestino);
        movEntrada.setCampania(campania);
        movEntrada.setArticulo(articulo);
        movEntrada.setCantidad(cantidad);
        movEntrada.setUsuario(usuario);
        movEntrada.setTransferencia(transferencia);
        movimientoRepository.save(movEntrada);

        return transferencia;
    }

    /**
     * Registra un ajuste de inventario (positivo o negativo) con motivo obligatorio.
     */
    public Movimiento registrarAjuste(Integer centroId, Integer campaniaId, Integer articuloId,
                                      BigDecimal cantidad, boolean esPositivo,
                                      Integer usuarioId, MotivoMovimiento motivo, String detalle) {
        validarCantidadPositiva(cantidad);
        if (motivo == null) {
            throw new IllegalArgumentException("El motivo es estrictamente obligatorio para registrar un ajuste.");
        }

        if (!esPositivo) {
            inventarioService.validarStockSuficiente(centroId, campaniaId, articuloId, cantidad);
        }

        Centro centro = obtenerCentro(centroId);
        Campania campania = obtenerCampania(campaniaId);
        Articulo articulo = obtenerArticulo(articuloId);
        Usuario usuario = obtenerUsuario(usuarioId);

        Movimiento mov = new Movimiento();
        mov.setTipo(esPositivo ? TipoMovimiento.AJUSTE_POSITIVO : TipoMovimiento.AJUSTE_NEGATIVO);
        mov.setCentro(centro);
        mov.setCampania(campania);
        mov.setArticulo(articulo);
        mov.setCantidad(cantidad);
        mov.setUsuario(usuario);
        mov.setMotivo(motivo);
        mov.setMotivoDetalle(detalle);

        return movimientoRepository.save(mov);
    }

    @Transactional(readOnly = true)
    public List<Movimiento> listarPorCentro(Integer centroId) {
        return movimientoRepository.findByCentroIdOrderByIdDesc(centroId);
    }

    @Transactional(readOnly = true)
    public List<Movimiento> listarPorCampania(Integer campaniaId) {
        return movimientoRepository.findByCampaniaIdOrderByFechaDesc(campaniaId);
    }

    private void validarCantidadPositiva(BigDecimal cantidad) {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
    }

    private Centro obtenerCentro(Integer id) {
        return centroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Centro no encontrado con ID: " + id));
    }

    private Campania obtenerCampania(Integer id) {
        return campaniaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campaña no encontrada con ID: " + id));
    }

    private Articulo obtenerArticulo(Integer id) {
        return articuloRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artículo no encontrado con ID: " + id));
    }

    private Usuario obtenerUsuario(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }
}
