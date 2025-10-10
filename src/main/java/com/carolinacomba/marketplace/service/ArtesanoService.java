package com.carolinacomba.marketplace.service;

import com.carolinacomba.marketplace.model.Artesano;

public interface ArtesanoService {
    
    Artesano findById(Long id);
    
    Artesano save(Artesano artesano);
}
