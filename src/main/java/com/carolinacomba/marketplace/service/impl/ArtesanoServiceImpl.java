package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.Usuario;
import com.carolinacomba.marketplace.repository.ArtesanoRepository;
import com.carolinacomba.marketplace.repository.ItemOrdenRepository;
import com.carolinacomba.marketplace.service.ArtesanoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ArtesanoServiceImpl implements ArtesanoService {

    private final ArtesanoRepository artesanoRepository;
    private final ItemOrdenRepository itemOrdenRepository;

    @Override
    public Artesano findById(Long id) {
        return artesanoRepository.findById(id).orElse(null);
    }

    @Override
    public Artesano save(Artesano artesano) {
        return artesanoRepository.save(artesano);
    }

    @Override
    public List<Map<String, Object>> obtenerVentasPorArtesano(Usuario artesano) {
        System.out.println("=== OBTENER VENTAS POR ARTESANO ===");
        System.out.println("Artesano ID: " + artesano.getId());
        System.out.println("Artesano nombre: " + artesano.getNombre());
        
        List<Map<String, Object>> ventas = new ArrayList<>();
        
        try {
            // Obtener todos los items de órdenes donde el producto pertenece al artesano
            System.out.println("Ejecutando consulta findVentasPorArtesano...");
            List<Object[]> resultados = itemOrdenRepository.findVentasPorArtesano(artesano.getId());
            System.out.println("Resultados encontrados: " + resultados.size());
            
            for (Object[] resultado : resultados) {
                Map<String, Object> venta = new HashMap<>();
                venta.put("ordenId", resultado[0]);
                venta.put("productoId", resultado[1]);
                venta.put("productoNombre", resultado[2]);
                venta.put("cantidad", resultado[3]);
                venta.put("precioUnitario", resultado[4]);
                venta.put("subtotal", resultado[5]);
                venta.put("estadoOrden", resultado[6]);
                venta.put("fechaCreacion", resultado[7]);
                venta.put("compradorNombre", resultado[8]);
                venta.put("compradorEmail", resultado[9]);
                ventas.add(venta);
                
                System.out.println("Venta agregada: " + resultado[2] + " - " + resultado[8]);
            }
        } catch (Exception e) {
            System.out.println("ERROR en obtenerVentasPorArtesano: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Total ventas retornadas: " + ventas.size());
        return ventas;
    }

    @Override
    public List<Map<String, Object>> obtenerVentasPorArtesanoYEstado(Usuario artesano, String estado) {
        List<Map<String, Object>> ventas = new ArrayList<>();
        
        // Obtener ventas filtradas por estado
        List<Object[]> resultados = itemOrdenRepository.findVentasPorArtesanoYEstado(artesano.getId(), estado);
        
        for (Object[] resultado : resultados) {
            Map<String, Object> venta = new HashMap<>();
            venta.put("ordenId", resultado[0]);
            venta.put("productoId", resultado[1]);
            venta.put("productoNombre", resultado[2]);
            venta.put("cantidad", resultado[3]);
            venta.put("precioUnitario", resultado[4]);
            venta.put("subtotal", resultado[5]);
            venta.put("estadoOrden", resultado[6]);
            venta.put("fechaCreacion", resultado[7]);
            venta.put("compradorNombre", resultado[8]);
            venta.put("compradorEmail", resultado[9]);
            ventas.add(venta);
        }
        
        return ventas;
    }

    @Override
    public Map<String, Object> obtenerEstadisticasVentas(Usuario artesano) {
        System.out.println("=== OBTENER ESTADISTICAS VENTAS ===");
        System.out.println("Artesano ID: " + artesano.getId());
        
        Map<String, Object> estadisticas = new HashMap<>();
        
        try {
            // Obtener estadísticas básicas
            Object[] stats = itemOrdenRepository.findEstadisticasVentasPorArtesano(artesano.getId());
            System.out.println("Stats array: " + (stats != null ? java.util.Arrays.toString(stats) : "null"));
            System.out.println("Stats length: " + (stats != null ? stats.length : "null"));
            
            // Verificar si stats es un array anidado
            if (stats != null && stats.length == 1 && stats[0] instanceof Object[]) {
                System.out.println("Detectado array anidado, extrayendo primer elemento");
                Object[] innerStats = (Object[]) stats[0];
                System.out.println("Inner stats: " + java.util.Arrays.toString(innerStats));
                System.out.println("Inner stats length: " + innerStats.length);
                
                if (innerStats.length >= 4) {
                    estadisticas.put("totalVentas", innerStats[0] != null ? innerStats[0] : 0);
                    estadisticas.put("totalIngresos", innerStats[1] != null ? innerStats[1] : BigDecimal.ZERO);
                    estadisticas.put("totalProductosVendidos", innerStats[2] != null ? innerStats[2] : 0);
                    estadisticas.put("ordenesPagadas", innerStats[3] != null ? innerStats[3] : 0);
                } else {
                    System.out.println("Array interno muy corto, usando valores por defecto");
                    estadisticas.put("totalVentas", 0);
                    estadisticas.put("totalIngresos", BigDecimal.ZERO);
                    estadisticas.put("totalProductosVendidos", 0);
                    estadisticas.put("ordenesPagadas", 0);
                }
            } else if (stats != null && stats.length >= 4) {
                estadisticas.put("totalVentas", stats[0] != null ? stats[0] : 0);
                estadisticas.put("totalIngresos", stats[1] != null ? stats[1] : BigDecimal.ZERO);
                estadisticas.put("totalProductosVendidos", stats[2] != null ? stats[2] : 0);
                estadisticas.put("ordenesPagadas", stats[3] != null ? stats[3] : 0);
            } else {
                System.out.println("No hay estadísticas disponibles, usando valores por defecto");
                estadisticas.put("totalVentas", 0);
                estadisticas.put("totalIngresos", BigDecimal.ZERO);
                estadisticas.put("totalProductosVendidos", 0);
                estadisticas.put("ordenesPagadas", 0);
            }
        } catch (Exception e) {
            System.out.println("ERROR en obtenerEstadisticasVentas: " + e.getMessage());
            e.printStackTrace();
            estadisticas.put("totalVentas", 0);
            estadisticas.put("totalIngresos", BigDecimal.ZERO);
            estadisticas.put("totalProductosVendidos", 0);
            estadisticas.put("ordenesPagadas", 0);
        }
        
        System.out.println("Estadísticas finales: " + estadisticas);
        return estadisticas;
    }
}
