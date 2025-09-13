package com.carolinacomba.marketplace.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioRolRequest {
    @NotNull(message = "El rol es requerido")
    private String rol;
}
