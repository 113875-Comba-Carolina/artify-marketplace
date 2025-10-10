package com.carolinacomba.marketplace.dto;

import com.carolinacomba.marketplace.model.CategoriaProducto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoWithImageRequest {
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal precio;
    
    @NotNull(message = "La categoría es obligatoria")
    private CategoriaProducto categoria;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    // Archivo de imagen (opcional)
    private MultipartFile imagen;
    
    // URL de imagen existente (opcional, para actualizaciones)
    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    private String imagenUrl;
    
    /**
     * Convierte este DTO a ProductoRequest estándar
     * @return ProductoRequest con la URL de imagen
     */
    public ProductoRequest toProductoRequest() {
        ProductoRequest request = new ProductoRequest();
        request.setNombre(this.nombre);
        request.setDescripcion(this.descripcion);
        request.setPrecio(this.precio);
        request.setCategoria(this.categoria);
        request.setStock(this.stock);
        request.setImagenUrl(this.imagenUrl);
        return request;
    }
    
    /**
     * Verifica si se proporcionó una imagen
     * @return true si hay una imagen, false en caso contrario
     */
    public boolean hasImage() {
        return imagen != null && !imagen.isEmpty();
    }
    
    /**
     * Verifica si se proporcionó una URL de imagen
     * @return true si hay una URL de imagen, false en caso contrario
     */
    public boolean hasImageUrl() {
        return imagenUrl != null && !imagenUrl.trim().isEmpty();
    }
}
