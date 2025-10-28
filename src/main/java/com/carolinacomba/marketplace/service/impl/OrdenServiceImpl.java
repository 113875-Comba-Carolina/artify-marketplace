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
import java.math.RoundingMode;
import java.util.ArrayList;
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

    @Override
    @Transactional
    public void reducirStockProductos(Long ordenId) {
        try {
            Orden orden = ordenRepository.findById(ordenId)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + ordenId));
            
            for (ItemOrden item : orden.getItems()) {
                Producto producto = item.getProducto();
                Integer cantidadComprada = item.getCantidad();
                
                boolean stockReducido = producto.reducirStock(cantidadComprada);
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                .map(item -> {
                    Usuario artesano = item.getProducto().getUsuario();
                    return ItemOrdenResponse.builder()
                            .id(item.getId())
                            .productoId(item.getProducto().getId())
                            .nombreProducto(item.getProducto().getNombre())
                            .imagenUrl(item.getProducto().getImagenUrl())
                            .categoria(item.getProducto().getCategoria().toString())
                            .cantidad(item.getCantidad())
                            .precioUnitario(item.getPrecioUnitario())
                            .subtotal(item.getSubtotal())
                            .artesanoNombre(artesano.getNombre())
                            .artesanoEmail(artesano.getEmail())
                            .artesanoTelefono(artesano.getTelefono())
                            .build();
                })
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

    @Override
    public BuyerStatisticsResponse obtenerEstadisticasComprador(Usuario usuario) {
        // Obtener estadísticas generales
        Object[] estadisticasGenerales = ordenRepository.findEstadisticasComprador(usuario.getId());
        
        BuyerStatisticsResponse.BuyerStatisticsResponseBuilder builder = BuyerStatisticsResponse.builder();
        
        if (estadisticasGenerales != null && estadisticasGenerales.length >= 4) {
            builder.totalOrdenes(((Number) estadisticasGenerales[0]).longValue())
                    .totalGastado(convertirABigDecimal(estadisticasGenerales[1]))
                    .totalProductos(((Number) estadisticasGenerales[2]).longValue())
                    .promedioPorCompra(convertirABigDecimal(estadisticasGenerales[3]));
        } else {
            builder.totalOrdenes(0L)
                    .totalGastado(BigDecimal.ZERO)
                    .totalProductos(0L)
                    .promedioPorCompra(BigDecimal.ZERO);
        }
        
        // Obtener categorías favoritas
        List<Object[]> categoriasFavoritas = ordenRepository.findCategoriasFavoritasPorUsuario(usuario.getId());
        builder.categoriasFavoritas(categoriasFavoritas.stream()
                .map(cat -> new BuyerStatisticsResponse.CategoriaFavorita(
                        (String) cat[0],
                        ((Number) cat[1]).longValue(),
                        convertirABigDecimal(cat[2])
                ))
                .collect(Collectors.toList()));
        
        // Obtener productos más comprados
        List<Object[]> productosMasComprados = ordenRepository.findProductosMasCompradosPorUsuario(usuario.getId());
        builder.productosMasComprados(productosMasComprados.stream()
                .map(prod -> new BuyerStatisticsResponse.ProductoMasComprado(
                        (String) prod[0],
                        (String) prod[1],
                        ((Number) prod[2]).longValue(),
                        convertirABigDecimal(prod[3])
                ))
                .collect(Collectors.toList()));
        
        // Obtener artesanos favoritos
        List<Object[]> artesanosFavoritos = ordenRepository.findArtesanosFavoritosPorUsuario(usuario.getId());
        builder.artesanosFavoritos(artesanosFavoritos.stream()
                .map(art -> new BuyerStatisticsResponse.ArtesanoFavorito(
                        (String) art[0],
                        ((Number) art[1]).longValue(),
                        convertirABigDecimal(art[2])
                ))
                .collect(Collectors.toList()));
        
        return builder.build();
    }
    
    private BigDecimal convertirABigDecimal(Object valor) {
        if (valor == null) {
            return BigDecimal.ZERO;
        }
        if (valor instanceof BigDecimal) {
            return (BigDecimal) valor;
        }
        if (valor instanceof Number) {
            return BigDecimal.valueOf(((Number) valor).doubleValue()).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
