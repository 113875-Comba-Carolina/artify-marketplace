package com.carolinacomba.marketplace.service;

import java.util.List;
import java.util.Map;

public interface AdminService {
    
    /**
     * Obtiene reportes completos de la plataforma
     * @return Map con todas las estadísticas
     */
    Map<String, Object> obtenerReportesCompletos();
    
    /**
     * Obtiene estadísticas de usuarios
     * @return Map con estadísticas de usuarios
     */
    Map<String, Object> obtenerEstadisticasUsuarios();
    
    /**
     * Obtiene estadísticas de ventas
     * @return Map con estadísticas de ventas
     */
    Map<String, Object> obtenerEstadisticasVentas();
    
    /**
     * Obtiene top artesanos por ventas
     * @return Lista de artesanos con sus estadísticas
     */
    List<Map<String, Object>> obtenerTopArtesanos();
    
    /**
     * Obtiene productos más vendidos
     * @return Lista de productos con sus estadísticas
     */
    List<Map<String, Object>> obtenerTopProductos();
    
    /**
     * Obtiene ventas por categoría
     * @return Lista de categorías con sus estadísticas
     */
    List<Map<String, Object>> obtenerVentasPorCategoria();
    
    /**
     * Obtiene actividad reciente de la plataforma
     * @return Lista de actividades recientes
     */
    List<Map<String, Object>> obtenerActividadReciente();
}
