package com.carolinacomba.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerStatisticsResponse {
    private Long totalOrdenes;
    private BigDecimal totalGastado;
    private Long totalProductos;
    private BigDecimal promedioPorCompra;
    private List<CategoriaFavorita> categoriasFavoritas;
    private List<ProductoMasComprado> productosMasComprados;
    private List<ArtesanoFavorito> artesanosFavoritos;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriaFavorita {
        private String categoria;
        private Long cantidadComprada;
        private BigDecimal totalGastado;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoMasComprado {
        private String nombre;
        private String imagenUrl;
        private Long totalComprado;
        private BigDecimal totalGastado;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtesanoFavorito {
        private String nombre;
        private Long ordenesConArtesano;
        private BigDecimal totalGastado;
    }
}

