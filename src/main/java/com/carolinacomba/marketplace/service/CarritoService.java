package com.carolinacomba.marketplace.service;

import com.carolinacomba.marketplace.dto.CarritoResponse;
import com.carolinacomba.marketplace.dto.ItemCarritoRequest;
import com.carolinacomba.marketplace.dto.ItemCarritoResponse;
import com.carolinacomba.marketplace.model.Carrito;
import com.carolinacomba.marketplace.model.ItemCarrito;
import com.carolinacomba.marketplace.model.Producto;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.repository.CarritoRepository;
import com.carolinacomba.marketplace.repository.ItemCarritoRepository;
import com.carolinacomba.marketplace.repository.ProductoRepository;
import com.carolinacomba.marketplace.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoService {
    
    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Obtiene el carrito de un usuario. Si no existe, lo crea.
     */
    @Transactional
    public CarritoResponse obtenerCarrito(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Carrito carrito = carritoRepository.findByUsuario(usuario)
            .orElseGet(() -> {
                Carrito nuevoCarrito = new Carrito();
                nuevoCarrito.setUsuario(usuario);
                return carritoRepository.save(nuevoCarrito);
            });
        
        return convertirACarritoResponse(carrito);
    }
    
    /**
     * Agrega un producto al carrito
     */
    @Transactional
    public CarritoResponse agregarAlCarrito(String email, ItemCarritoRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Producto producto = productoRepository.findById(request.getProductoId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Validar que haya stock disponible
        if (!producto.isDisponibleParaCompra()) {
            throw new RuntimeException("Producto no disponible para compra");
        }
        
        if (producto.getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
        }
        
        // Obtener o crear carrito
        Carrito carrito = carritoRepository.findByUsuario(usuario)
            .orElseGet(() -> {
                Carrito nuevoCarrito = new Carrito();
                nuevoCarrito.setUsuario(usuario);
                return carritoRepository.save(nuevoCarrito);
            });
        
        // Buscar si el producto ya está en el carrito
        ItemCarrito itemExistente = itemCarritoRepository
            .findByCarritoIdAndProductoId(carrito.getId(), producto.getId())
            .orElse(null);
        
        if (itemExistente != null) {
            // Validar que la nueva cantidad no exceda el stock
            int nuevaCantidad = itemExistente.getCantidad() + request.getCantidad();
            if (nuevaCantidad > producto.getStock()) {
                throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock() + 
                    ", Ya en carrito: " + itemExistente.getCantidad());
            }
            itemExistente.setCantidad(nuevaCantidad);
            itemCarritoRepository.save(itemExistente);
        } else {
            // Crear nuevo item
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(request.getCantidad());
            itemCarritoRepository.save(nuevoItem);
        }
        
        // Recargar el carrito con los items actualizados
        carrito = carritoRepository.findById(carrito.getId())
            .orElseThrow(() -> new RuntimeException("Error al recargar carrito"));
        
        return convertirACarritoResponse(carrito);
    }
    
    /**
     * Actualiza la cantidad de un item en el carrito
     */
    @Transactional
    public CarritoResponse actualizarCantidad(String email, Long productoId, Integer nuevaCantidad) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Carrito carrito = carritoRepository.findByUsuario(usuario)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        if (nuevaCantidad <= 0) {
            // Si la cantidad es 0 o negativa, eliminar el item
            itemCarritoRepository.deleteByCarritoIdAndProductoId(carrito.getId(), productoId);
        } else {
            ItemCarrito item = itemCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), productoId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));
            
            Producto producto = item.getProducto();
            
            // Validar stock
            if (nuevaCantidad > producto.getStock()) {
                throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
            }
            
            item.setCantidad(nuevaCantidad);
            itemCarritoRepository.save(item);
        }
        
        // Recargar el carrito
        carrito = carritoRepository.findById(carrito.getId())
            .orElseThrow(() -> new RuntimeException("Error al recargar carrito"));
        
        return convertirACarritoResponse(carrito);
    }
    
    /**
     * Elimina un producto del carrito
     */
    @Transactional
    public CarritoResponse eliminarDelCarrito(String email, Long productoId) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Carrito carrito = carritoRepository.findByUsuario(usuario)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        itemCarritoRepository.deleteByCarritoIdAndProductoId(carrito.getId(), productoId);
        
        // Recargar el carrito
        carrito = carritoRepository.findById(carrito.getId())
            .orElseThrow(() -> new RuntimeException("Error al recargar carrito"));
        
        return convertirACarritoResponse(carrito);
    }
    
    /**
     * Limpia todo el carrito
     */
    @Transactional
    public CarritoResponse limpiarCarrito(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Carrito carrito = carritoRepository.findByUsuario(usuario)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        itemCarritoRepository.deleteByCarritoId(carrito.getId());
        
        // Recargar el carrito
        carrito = carritoRepository.findById(carrito.getId())
            .orElseThrow(() -> new RuntimeException("Error al recargar carrito"));
        
        return convertirACarritoResponse(carrito);
    }
    
    /**
     * Sincroniza el carrito del localStorage con el de la base de datos
     * Útil cuando un usuario inicia sesión con items en su carrito local
     */
    @Transactional
    public CarritoResponse sincronizarCarrito(String email, List<ItemCarritoRequest> itemsLocales) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Obtener o crear carrito
        Carrito carrito = carritoRepository.findByUsuario(usuario)
            .orElseGet(() -> {
                Carrito nuevoCarrito = new Carrito();
                nuevoCarrito.setUsuario(usuario);
                return carritoRepository.save(nuevoCarrito);
            });
        
        // Agregar items locales al carrito del servidor
        for (ItemCarritoRequest itemLocal : itemsLocales) {
            try {
                Producto producto = productoRepository.findById(itemLocal.getProductoId())
                    .orElse(null);
                
                if (producto != null && producto.isDisponibleParaCompra()) {
                    ItemCarrito itemExistente = itemCarritoRepository
                        .findByCarritoIdAndProductoId(carrito.getId(), producto.getId())
                        .orElse(null);
                    
                    if (itemExistente != null) {
                        // Sumar cantidades, respetando el stock
                        int nuevaCantidad = Math.min(
                            itemExistente.getCantidad() + itemLocal.getCantidad(),
                            producto.getStock()
                        );
                        itemExistente.setCantidad(nuevaCantidad);
                        itemCarritoRepository.save(itemExistente);
                    } else {
                        // Crear nuevo item, respetando el stock
                        int cantidadFinal = Math.min(itemLocal.getCantidad(), producto.getStock());
                        if (cantidadFinal > 0) {
                            ItemCarrito nuevoItem = new ItemCarrito();
                            nuevoItem.setCarrito(carrito);
                            nuevoItem.setProducto(producto);
                            nuevoItem.setCantidad(cantidadFinal);
                            itemCarritoRepository.save(nuevoItem);
                        }
                    }
                }
            } catch (Exception e) {
                // Continuar con el siguiente item si hay algún error
                System.err.println("Error al sincronizar item: " + e.getMessage());
            }
        }
        
        // Recargar el carrito
        carrito = carritoRepository.findById(carrito.getId())
            .orElseThrow(() -> new RuntimeException("Error al recargar carrito"));
        
        return convertirACarritoResponse(carrito);
    }
    
    /**
     * Convierte un Carrito a CarritoResponse
     */
    private CarritoResponse convertirACarritoResponse(Carrito carrito) {
        List<ItemCarritoResponse> items = carrito.getItems().stream()
            .map(this::convertirAItemCarritoResponse)
            .collect(Collectors.toList());
        
        BigDecimal total = items.stream()
            .map(ItemCarritoResponse::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int cantidadTotal = items.stream()
            .mapToInt(ItemCarritoResponse::getCantidad)
            .sum();
        
        return CarritoResponse.builder()
            .id(carrito.getId())
            .usuarioId(carrito.getUsuario().getId())
            .items(items)
            .cantidadTotal(cantidadTotal)
            .totalCarrito(total)
            .build();
    }
    
    /**
     * Convierte un ItemCarrito a ItemCarritoResponse
     */
    private ItemCarritoResponse convertirAItemCarritoResponse(ItemCarrito item) {
        Producto producto = item.getProducto();
        BigDecimal subtotal = producto.getPrecio().multiply(new BigDecimal(item.getCantidad()));
        
        ItemCarritoResponse.ProductoCarritoDTO productoDTO = ItemCarritoResponse.ProductoCarritoDTO.builder()
            .id(producto.getId())
            .nombre(producto.getNombre())
            .precio(producto.getPrecio())
            .imagenUrl(producto.getImagenUrl())
            .categoria(producto.getCategoria().name())
            .stockDisponible(producto.getStock())
            .build();
        
        return ItemCarritoResponse.builder()
            .id(item.getId())
            .producto(productoDTO)
            .cantidad(item.getCantidad())
            .subtotal(subtotal)
            .build();
    }
}

