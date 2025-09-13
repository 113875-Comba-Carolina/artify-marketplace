package com.carolinacomba.marketplace.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "clientes")
@DiscriminatorValue("CLIENTE")
@Data
@EqualsAndHashCode(callSuper = true)
public class Cliente extends Usuario {

    public Cliente() {
        super();
        setRol(Rol.CLIENTE);
    }

    public Cliente(String nombre, String email, String contraseña) {
        super(nombre, email, contraseña, Rol.CLIENTE);
    }

} 