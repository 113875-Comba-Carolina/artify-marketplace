package com.carolinacomba.marketplace.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "artesanos")
@DiscriminatorValue("ARTESANO")
@Data
@EqualsAndHashCode(callSuper = true)
public class Artesano extends Usuario {

    @Size(max = 200, message = "El nombre del emprendimiento no puede exceder 200 caracteres")
    @Column(name = "nombre_emprendimiento", length = 200)
    private String nombreEmprendimiento;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Column(length = 1000)
    private String descripcion;

    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    @Column(length = 100)
    private String ubicacion;

    public Artesano() {
        super();
        setRol(Rol.ARTESANO);
    }

    public Artesano(String nombre, String email, String contrasena, 
                   String nombreEmprendimiento, String descripcion, String ubicacion) {
        super(nombre, email, contrasena, Rol.ARTESANO);
        this.nombreEmprendimiento = nombreEmprendimiento;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
    }
} 