package com.portable.microservices.ms_tracking.picking.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.portable.microservices.ms_tracking.picking.domain.model.RutaPick;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.RutaPickPersistencePortOut;
import com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity.RutaPickJpaEntity;
import com.portable.microservices.ms_tracking.picking.infrastructure.persistence.repository.RutaPickJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RutaPickPersistenceAdapter implements RutaPickPersistencePortOut {
    private final RutaPickJpaRepository repository;
    @Override
    public RutaPick save(RutaPick ruta) {
        var entity = new RutaPickJpaEntity();
        entity.setIdRuta(ruta.idRuta());
        entity.setIdOrden(ruta.idOrden());
        entity.setPathSeq(ruta.pathSeq());
        entity.setDistanciaEstimada(ruta.distanciaEstimada());
        entity.setFecCreacion(ruta.fecCreacion());

        var saved = repository.save(entity);

        return new RutaPick(
                saved.getIdRuta(),
                saved.getIdOrden(),
                saved.getPathSeq(),
                saved.getDistanciaEstimada(),
                saved.getFecCreacion()
        );
    }

    @Override
    public Optional<RutaPick> findByIdOrden(UUID idOrden) {
        return repository.findByIdOrden(idOrden).map(e -> new RutaPick(
                e.getIdRuta(), e.getIdOrden(), e.getPathSeq(),
                e.getDistanciaEstimada(), e.getFecCreacion()
        ));
    }
    
}
