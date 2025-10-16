package com.carolinacomba.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WelcomeEmailData {
    private String nombre;
    private String email;
    private String tipoUsuario;
}
