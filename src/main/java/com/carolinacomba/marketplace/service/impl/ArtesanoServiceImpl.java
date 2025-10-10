package com.carolinacomba.marketplace.service.impl;

import com.carolinacomba.marketplace.model.Artesano;
import com.carolinacomba.marketplace.repository.ArtesanoRepository;
import com.carolinacomba.marketplace.service.ArtesanoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ArtesanoServiceImpl implements ArtesanoService {

    private final ArtesanoRepository artesanoRepository;

    @Override
    public Artesano findById(Long id) {
        return artesanoRepository.findById(id).orElse(null);
    }

    @Override
    public Artesano save(Artesano artesano) {
        return artesanoRepository.save(artesano);
    }
}
