package com.carolinacomba.marketplace.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoriaProducto {
    CERAMICA("Cerámica"),
    METALES("Metales"),
    MATE("Mates y accesorios"),
    AROMAS_VELAS("Aromas y velas"),
    TEXTILES("Textiles"),
    CUERO("Cuero"),
    MADERA("Madera"),
    VIDRIO("Vidrio"),
    JOYERIA_ARTESANAL("Joyería artesanal"),
    CESTERIA_FIBRAS("Cestería y fibras"),
    ARTE_PINTURA("Arte y pintura"),
    OTROS("Otros");

    private final String nombre;
}

