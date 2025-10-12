package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.model.EstadoOrden;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.repository.*;
import com.carolinacomba.marketplace.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.Arrays;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final OrdenRepository ordenRepository;
    private final ItemOrdenRepository itemOrdenRepository;

    @Override
    public Map<String, Object> obtenerReportesCompletos() {
        Map<String, Object> reportes = new HashMap<>();
        
        // Estadísticas generales
        reportes.putAll(obtenerEstadisticasUsuarios());
        reportes.putAll(obtenerEstadisticasVentas());
        
        // Top artesanos y productos
        reportes.put("topArtesanos", obtenerTopArtesanos());
        reportes.put("topProductos", obtenerTopProductos());
        reportes.put("ventasPorCategoria", obtenerVentasPorCategoria());
        reportes.put("actividadReciente", obtenerActividadReciente());
        
        return reportes;
    }

    @Override
    public Map<String, Object> obtenerEstadisticasUsuarios() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        try {
            // Total de usuarios
            Long totalUsuarios = usuarioRepository.count();
            estadisticas.put("totalUsuarios", totalUsuarios);
            
            // Total de artesanos
            Long totalArtesanos = usuarioRepository.countByRol(Usuario.Rol.ARTESANO);
            estadisticas.put("totalArtesanos", totalArtesanos);
            
            // Total de productos activos
            Long totalProductos = productoRepository.countByEsActivoTrue();
            estadisticas.put("totalProductos", totalProductos);
            
        } catch (Exception e) {
            estadisticas.put("totalUsuarios", 0);
            estadisticas.put("totalArtesanos", 0);
            estadisticas.put("totalProductos", 0);
        }
        
        return estadisticas;
    }

    @Override
    public Map<String, Object> obtenerEstadisticasVentas() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        try {
            // Total de órdenes
            Long totalOrdenes = ordenRepository.count();
            estadisticas.put("totalOrdenes", totalOrdenes);
            
            // Órdenes por estado
            Long ordenesPagadas = ordenRepository.countByEstado(EstadoOrden.PAGADO);
            Long ordenesPendientes = ordenRepository.countByEstado(EstadoOrden.PENDIENTE);
            Long ordenesCanceladas = ordenRepository.countByEstado(EstadoOrden.CANCELADO);
            
            estadisticas.put("ordenesPagadas", ordenesPagadas);
            estadisticas.put("ordenesPendientes", ordenesPendientes);
            estadisticas.put("ordenesCanceladas", ordenesCanceladas);
            
        } catch (Exception e) {
            estadisticas.put("totalOrdenes", 0);
            estadisticas.put("ordenesPagadas", 0);
            estadisticas.put("ordenesPendientes", 0);
            estadisticas.put("ordenesCanceladas", 0);
        }
        
        return estadisticas;
    }

    @Override
    public List<Map<String, Object>> obtenerTopArtesanos() {
        List<Map<String, Object>> topArtesanos = new ArrayList<>();
        
        try {
            List<Object[]> resultados = itemOrdenRepository.findTopArtesanos();
            
            for (Object[] resultado : resultados) {
                Map<String, Object> artesano = new HashMap<>();
                artesano.put("id", resultado[0]);
                artesano.put("nombre", resultado[1]);
                artesano.put("email", resultado[2]);
                artesano.put("nombreEmprendimiento", resultado[3]);
                artesano.put("totalVentas", resultado[4]);
                artesano.put("ingresos", resultado[5]);
                artesano.put("totalProductos", resultado[6]);
                topArtesanos.add(artesano);
            }
            
        } catch (Exception e) {
            System.out.println("Error obteniendo top artesanos: " + e.getMessage());
        }
        
        return topArtesanos;
    }

    @Override
    public List<Map<String, Object>> obtenerTopProductos() {
        List<Map<String, Object>> topProductos = new ArrayList<>();
        
        try {
            List<Object[]> resultados = itemOrdenRepository.findTopProductos();
            
            for (Object[] resultado : resultados) {
                Map<String, Object> producto = new HashMap<>();
                producto.put("id", resultado[0]);
                producto.put("nombre", resultado[1]);
                producto.put("categoria", resultado[2]);
                producto.put("imagenUrl", resultado[3]);
                producto.put("artesanoNombre", resultado[4]);
                producto.put("cantidadVendida", resultado[5]);
                producto.put("ingresos", resultado[6]);
                topProductos.add(producto);
            }
            
        } catch (Exception e) {
            System.out.println("Error obteniendo top productos: " + e.getMessage());
        }
        
        return topProductos;
    }

    @Override
    public List<Map<String, Object>> obtenerVentasPorCategoria() {
        List<Map<String, Object>> ventasPorCategoria = new ArrayList<>();
        
        try {
            List<Object[]> resultados = itemOrdenRepository.findVentasPorCategoria();
            
            for (Object[] resultado : resultados) {
                Map<String, Object> categoria = new HashMap<>();
                categoria.put("categoria", resultado[0]);
                categoria.put("totalVentas", resultado[1]);
                categoria.put("ingresos", resultado[2]);
                categoria.put("totalProductos", resultado[3]);
                ventasPorCategoria.add(categoria);
            }
            
        } catch (Exception e) {
            System.out.println("Error obteniendo ventas por categoría: " + e.getMessage());
        }
        
        return ventasPorCategoria;
    }

    @Override
    public List<Map<String, Object>> obtenerActividadReciente() {
        List<Map<String, Object>> actividad = new ArrayList<>();
        
        try {
            // Actividad reciente de órdenes
            List<Object[]> ordenesRecientes = ordenRepository.findActividadReciente();
            
            for (Object[] orden : ordenesRecientes) {
                Map<String, Object> actividadItem = new HashMap<>();
                actividadItem.put("id", orden[0]);
                actividadItem.put("tipo", "VENTA");
                actividadItem.put("descripcion", "Nueva venta: " + orden[1] + " por $" + orden[2]);
                actividadItem.put("fecha", orden[3]);
                actividad.add(actividadItem);
            }
            
        } catch (Exception e) {
            System.out.println("Error obteniendo actividad reciente: " + e.getMessage());
        }
        
        return actividad;
    }
}
