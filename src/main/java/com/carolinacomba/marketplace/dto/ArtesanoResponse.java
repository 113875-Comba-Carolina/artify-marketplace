package com.carolinacomba.marketplace.dto;

import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.model.Usuario;
import lombok.Data;

@Data
public class ArtesanoResponse {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private String nombreEmprendimiento;
    private String descripcion;
    private String ubicacion;
    private String telefono;

    public ArtesanoResponse(Artesano artesano) {
        this.id = artesano.getId();
        this.nombre = artesano.getNombre();
        this.email = artesano.getEmail();
        this.rol = artesano.getRol().toString();
        this.nombreEmprendimiento = artesano.getNombreEmprendimiento();
        this.descripcion = artesano.getDescripcion();
        this.ubicacion = artesano.getUbicacion();
        this.telefono = artesano.getTelefono();
    }

    public ArtesanoResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        this.rol = usuario.getRol().toString();
        this.telefono = usuario.getTelefono();
        
        // Solo asignar campos del emprendimiento si es un artesano
        if (usuario instanceof Artesano) {
            Artesano artesano = (Artesano) usuario;
            this.nombreEmprendimiento = artesano.getNombreEmprendimiento();
            this.descripcion = artesano.getDescripcion();
            this.ubicacion = artesano.getUbicacion();
        }
    }

    public ArtesanoResponse(Usuario usuario, String nombreEmprendimiento, String descripcion, String ubicacion) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        this.rol = usuario.getRol().toString();
        this.telefono = usuario.getTelefono();
        this.nombreEmprendimiento = nombreEmprendimiento;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
    }
}
