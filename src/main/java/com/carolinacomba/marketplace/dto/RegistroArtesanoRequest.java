package com.carolinacomba.marketplace.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroArtesanoRequest {

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email es requerido")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "La contrasena es requerida")
    @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
    private String password;

    @Size(max = 100, message = "El nombre del emprendimiento no puede exceder 100 caracteres")
    private String nombreEmprendimiento;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    private String ubicacion;

    @NotBlank(message = "El teléfono es requerido")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;
}