package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.dto.*;
import com.carolinacomba.marketplace.model.*;
import com.carolinacomba.marketplace.repository.OrdenRepository;
import com.carolinacomba.marketplace.repository.ProductoRepository;
import com.carolinacomba.marketplace.service.OrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenServiceImpl implements OrdenService {

    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public Orden crearOrden(Usuario usuario, String externalReference, List<CarritoItem> items) {
        // Crear la orden
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setExternalReference(externalReference);
        orden.setEstado(EstadoOrden.PENDIENTE);
        
        // Calcular total
        BigDecimal total = items.stream()
                .map(item -> item.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        orden.setTotal(total);
        
        // Guardar la orden
        Orden ordenGuardada = ordenRepository.save(orden);
        
        // Crear los items de la orden
        List<ItemOrden> itemOrdenes = items.stream()
                .map(item -> {
                    // Buscar producto por nombre si el ID es 0
                    Producto producto;
                    if (item.getProductoId() == 0L) {
                        producto = productoRepository.findByNombre(item.getNombreProducto())
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getNombreProducto()));
                    } else {
                        producto = productoRepository.findById(item.getProductoId())
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductoId()));
                    }
                    
                    ItemOrden itemOrden = new ItemOrden();
                    itemOrden.setOrden(ordenGuardada);
                    itemOrden.setProducto(producto);
                    itemOrden.setCantidad(item.getCantidad());
                    itemOrden.setPrecioUnitario(item.getPrecio());
                    itemOrden.calcularSubtotal();
                    
                    return itemOrden;
                })
                .collect(Collectors.toList());
        
        ordenGuardada.setItems(itemOrdenes);
        
        return ordenRepository.save(ordenGuardada);
    }

    @Override
    @Transactional
    public Orden actualizarEstadoOrden(String mercadoPagoId, String estado) {
        Orden orden = ordenRepository.findByMercadoPagoId(mercadoPagoId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + mercadoPagoId));
        
        // Mapear el estado de Mercado Pago a nuestro enum
        EstadoOrden estadoOrden = mapearEstadoMercadoPago(estado);
        orden.setEstado(estadoOrden);
        
        // Asegurar que el mercadoPagoId esté establecido
        if (orden.getMercadoPagoId() == null) {
            orden.setMercadoPagoId(mercadoPagoId);
        }
        
        return ordenRepository.save(orden);
    }

    @Override
    public List<OrdenResponse> obtenerOrdenesPorUsuario(Usuario usuario) {
        List<Orden> ordenes = ordenRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
        return ordenes.stream()
                .map(this::convertirAOrdenResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrdenResponse obtenerOrdenPorId(Long ordenId, Usuario usuario) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + ordenId));
        
        // Verificar que la orden pertenece al usuario
        if (!orden.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permisos para ver esta orden");
        }
        
        return convertirAOrdenResponse(orden);
    }

    @Override
    public OrdenResponse obtenerOrdenPorMercadoPagoId(String mercadoPagoId) {
        Orden orden = ordenRepository.findByMercadoPagoId(mercadoPagoId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + mercadoPagoId));
        
        return convertirAOrdenResponse(orden);
    }

    @Override
    public Orden obtenerOrdenPorExternalReference(String externalReference) {
        return ordenRepository.findByExternalReference(externalReference)
                .orElse(null);
    }

    @Override
    public List<Orden> obtenerTodasLasOrdenes() {
        return ordenRepository.findAll();
    }

    private EstadoOrden mapearEstadoMercadoPago(String estadoMercadoPago) {
        return switch (estadoMercadoPago.toLowerCase()) {
            case "approved" -> EstadoOrden.PAGADO;
            case "pending" -> EstadoOrden.PENDIENTE;
            case "cancelled" -> EstadoOrden.CANCELADO;
            case "refunded" -> EstadoOrden.REEMBOLSADO;
            default -> EstadoOrden.PENDIENTE;
        };
    }

    private OrdenResponse convertirAOrdenResponse(Orden orden) {
        List<ItemOrdenResponse> items = orden.getItems().stream()
                .map(item -> ItemOrdenResponse.builder()
                        .id(item.getId())
                        .productoId(item.getProducto().getId())
                        .nombreProducto(item.getProducto().getNombre())
                        .imagenUrl(item.getProducto().getImagenUrl())
                        .categoria(item.getProducto().getCategoria().toString())
                        .cantidad(item.getCantidad())
                        .precioUnitario(item.getPrecioUnitario())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrdenResponse.builder()
                .id(orden.getId())
                .mercadoPagoId(orden.getMercadoPagoId())
                .externalReference(orden.getExternalReference())
                .estado(orden.getEstado())
                .total(orden.getTotal())
                .fechaCreacion(orden.getFechaCreacion())
                .fechaActualizacion(orden.getFechaActualizacion())
                .nombreUsuario(orden.getUsuario().getNombre())
                .emailUsuario(orden.getUsuario().getEmail())
                .items(items)
                .build();
    }
}
