package org.example.vivesbankproject.movimientos.services;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

import org.example.vivesbankproject.cliente.exceptions.ClienteNotFoundByUser;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFound;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFoundByClienteGuid;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFoundByTarjetaId;
import org.example.vivesbankproject.cuenta.services.CuentaService;
import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.exceptions.domiciliacion.DuplicatedDomiciliacionException;
import org.example.vivesbankproject.movimientos.exceptions.domiciliacion.SaldoInsuficienteException;
import org.example.vivesbankproject.movimientos.exceptions.movimientos.ClienteHasNoMovements;
import org.example.vivesbankproject.movimientos.exceptions.movimientos.MovimientoNotFound;
import org.example.vivesbankproject.movimientos.exceptions.movimientos.NegativeAmount;
import org.example.vivesbankproject.movimientos.mappers.MovimientoMapper;
import org.example.vivesbankproject.movimientos.models.*;
import org.example.vivesbankproject.movimientos.repositories.DomiciliacionRepository;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFoundByNumero;
import org.example.vivesbankproject.tarjeta.service.TarjetaService;
import org.example.vivesbankproject.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@CacheConfig(cacheNames = {"Movimientos"})
public class MovimientosServiceImpl implements MovimientosService {

    private final ClienteService clienteService;
    private final MovimientosRepository movimientosRepository;
    private final DomiciliacionRepository domiciliacionRepository;
    private final CuentaService cuentaService;
    private final MovimientoMapper movimientosMapper;
    private final TarjetaService tarjetaService;




    @Autowired
    public MovimientosServiceImpl( CuentaService cuentaService, MovimientosRepository movimientosRepository, ClienteService clienteService, MovimientoMapper movimientosMapper, DomiciliacionRepository domiciliacionRepository, TarjetaService tarjetaService) {
        this.clienteService = clienteService;
        this.movimientosRepository = movimientosRepository;
        this.movimientosMapper = movimientosMapper;
        this.domiciliacionRepository = domiciliacionRepository;
        this.cuentaService = cuentaService;
        this.tarjetaService = tarjetaService;
    }

    @Override
    public Page<MovimientoResponse> getAll(Pageable pageable) {
        log.info("Encontrando todos los Movimientos");
        return movimientosRepository.findAll(pageable).map(movimientosMapper::toMovimientoResponse);
    }


    @Override
    @Cacheable
    public MovimientoResponse getById(ObjectId _id) {
        log.info("Encontrando Movimiento por id: {}", _id);
        return movimientosRepository.findById(_id)
                .map(movimientosMapper::toMovimientoResponse)
                .orElseThrow(() -> new MovimientoNotFound(_id));
    }

    @Override
    @Cacheable
    public MovimientoResponse getByGuid(String guidMovimiento) {
        log.info("Encontrando Movimiento por guid: {}", guidMovimiento);
        return movimientosRepository.findByGuid(guidMovimiento)
                .map(movimientosMapper::toMovimientoResponse)
                .orElseThrow(() -> new MovimientoNotFound(guidMovimiento));
    }

    @Override
    @Cacheable
    public MovimientoResponse getByClienteGuid(String ClienteGuid) {
        log.info("Encontrando Movimientos por idCliente: {}", ClienteGuid);
        clienteService.getById(ClienteGuid);
        return movimientosRepository.findMovimientosByClienteGuid(ClienteGuid)
                .map(movimientosMapper::toMovimientoResponse)
                .orElseThrow(() -> new ClienteHasNoMovements(ClienteGuid));
    }

    @Override
    @CachePut
    public MovimientoResponse save(MovimientoRequest movimientoRequest) {
        log.info("Guardando Movimiento: {}", movimientoRequest);
        clienteService.getById(movimientoRequest.getClienteGuid());
        Movimiento movimiento = movimientosMapper.toMovimiento(movimientoRequest);
        var savedMovimiento = movimientosRepository.save(movimiento);
        return movimientosMapper.toMovimientoResponse(savedMovimiento);
    }

    @Override
    public Domiciliacion saveDomiciliacion(User user, Domiciliacion domiciliacion) {
        log.info("Guardando Domiciliacion: {}", domiciliacion);

        // Validar que el cliente existe
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (cliente == null) {
            throw new ClienteNotFoundByUser(user.getGuid());
        }

        // Validar que la cuenta existe
        var clienteCuenta = cuentaService.getByIban(domiciliacion.getIbanOrigen());
        if (clienteCuenta == null) {
            throw new CuentaNotFound(domiciliacion.getIbanOrigen());
        }

        // Validar si la domiciliación ya existe
        var clienteDomiciliaciones = domiciliacionRepository.findByClienteGuid(cliente.getGuid());
        if (clienteDomiciliaciones.stream().anyMatch(d -> d.getIbanDestino().equals(domiciliacion.getIbanDestino()))) {
            throw new DuplicatedDomiciliacionException(domiciliacion.getIbanDestino());
        }

        // Validar que la cantidad es mayor que cero
        var cantidadDomiciliacion = new BigDecimal(domiciliacion.getCantidad().toString());
        if (cantidadDomiciliacion.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmount(cantidadDomiciliacion);
        }

        // Guardar la domiciliación
        domiciliacion.setUltimaEjecucion(LocalDateTime.now()); // Registro inicial
        domiciliacion.setClienteGuid(cliente.getGuid()); // Asigno el id del cliente al domiciliación
        Domiciliacion saved = domiciliacionRepository.save(domiciliacion);

        // Retornar respuesta
        return saved;
    }


    @Override
    public MovimientoResponse saveIngresoDeNomina(User user, IngresoDeNomina ingresoDeNomina) {
        log.info("Guardando Ingreso de Nomina: {}", ingresoDeNomina);

        // Validar que el cliente existe
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (cliente == null) {
            throw new ClienteNotFoundByUser(user.getGuid());
        }

        // Validar que la cuenta existe
        var clienteCuenta = cuentaService.getByIban(ingresoDeNomina.getIban_Destino());
        if (clienteCuenta == null) {
            throw new CuentaNotFound(ingresoDeNomina.getIban_Destino());
        }

        var empresaCuenta = cuentaService.getByIban(ingresoDeNomina.getIban_Origen());
        if (empresaCuenta == null) {
            throw new CuentaNotFound(ingresoDeNomina.getIban_Origen());
        }

        var cantidadNomina = new BigDecimal(ingresoDeNomina.getCantidad().toString());
        if (cantidadNomina.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmount(cantidadNomina);
        }

        // VALIDAR CIF



        BigDecimal saldoActual = new BigDecimal(empresaCuenta.getSaldo());
        // Validar saldo suficiente
        if (saldoActual.compareTo(cantidadNomina) < 0) {
            throw new SaldoInsuficienteException(empresaCuenta.getIban(), saldoActual);
        }


        Movimiento movimineto = Movimiento.builder()
                .ingresoDeNomina(ingresoDeNomina)
                .build();

        // Guardar el movimiento
        Movimiento saved = movimientosRepository.save(movimineto);
        return movimientosMapper.toMovimientoResponse(saved);
    }

    @Override
    public MovimientoResponse savePagoConTarjeta(User user, PagoConTarjeta pagoConTarjeta) {
        log.info("Guardando Pago con Tarjeta: {}", pagoConTarjeta);

        // Validar que el cliente existe
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (cliente == null) {
            throw new ClienteNotFoundByUser(user.getGuid());
        }

        // Validar que la tarjeta existe
        var clienteTarjeta = tarjetaService.getByNumeroTarjeta(pagoConTarjeta.getNumeroTarjeta());
        if (clienteTarjeta == null) {
            throw new TarjetaNotFoundByNumero(pagoConTarjeta.getNumeroTarjeta());
        }


        var clienteCuentas = cuentaService.getAllCuentasByClienteGuid(cliente.getGuid());
        if (clienteCuentas == null) {
            throw new CuentaNotFoundByClienteGuid(cliente.getGuid());
        }

        var cuentaAsociadaATarjeta = clienteCuentas.stream()
                .filter(c -> c.getTarjetaId().equals(clienteTarjeta.getGuid()))
                .findFirst()
                .orElseThrow(() -> new CuentaNotFoundByTarjetaId(clienteTarjeta.getGuid()));


        // Validar que la cantidad es mayor que cero
        var cantidadTarjeta = new BigDecimal(pagoConTarjeta.getCantidad().toString());
        if (cantidadTarjeta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmount(cantidadTarjeta);
        }

        BigDecimal saldoActual = new BigDecimal(cuentaAsociadaATarjeta.getSaldo());
        // Validar saldo suficiente
        if (saldoActual.compareTo(cantidadTarjeta) < 0) {
            throw new SaldoInsuficienteException(cuentaAsociadaATarjeta.getIban(), saldoActual);
        }

        Movimiento movimiento = Movimiento.builder()
                .pagoConTarjeta(pagoConTarjeta)
                .build();

        // Guardar el movimiento
        Movimiento saved = movimientosRepository.save(movimiento);
        return movimientosMapper.toMovimientoResponse(saved);

    }

    @Override
    public MovimientoResponse saveTransferencia(User user, Transferencia transferencia) {
        log.info("Guardando Transferencia: {}", transferencia);
        // Validar que el cliente existe
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (cliente == null) {
            throw new ClienteNotFoundByUser(user.getGuid());
        }

        // Validar que la cuenta existe
        var cuentaOrigen = cuentaService.getByIban(transferencia.getIban_Origen());
        if (cuentaOrigen == null) {
            throw new CuentaNotFound(transferencia.getIban_Origen());
        }

        var cuentaDestino = cuentaService.getByIban(transferencia.getIban_Destino());
        if (cuentaDestino == null) {
            throw new CuentaNotFound(transferencia.getIban_Destino());
        }

        // Validar que la cantidad es mayor que cero
        var cantidadTranseferencia = new BigDecimal(transferencia.getCantidad().toString());
        if (cantidadTranseferencia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmount(cantidadTranseferencia);
        }

        BigDecimal saldoActual = new BigDecimal(cuentaOrigen.getSaldo());
        // Validar saldo suficiente
        if (saldoActual.compareTo(cantidadTranseferencia) < 0) {
            throw new SaldoInsuficienteException(cuentaOrigen.getIban(), saldoActual);
        }

        Movimiento movimiento = Movimiento.builder()
                .transferencia(transferencia)
                .build();

        // Guardar el movimiento
        Movimiento saved = movimientosRepository.save(movimiento);
        return movimientosMapper.toMovimientoResponse(saved);
    }
}
