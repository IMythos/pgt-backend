package com.portable.microservices.ms_tracking.picking.infrastructure.persistence.adapter;

import org.springframework.stereotype.Component;

import com.portable.microservices.ms_tracking.picking.domain.model.AuditoriaPick;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.AuditoriaPickPersistencePortOut;
import com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity.AuditoriaPickJpaEntity;
import com.portable.microservices.ms_tracking.picking.infrastructure.persistence.repository.AuditoriaPickJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditoriaPickPersistenceAdapter implements AuditoriaPickPersistencePortOut {
    private final AuditoriaPickJpaRepository repository;

    @Override
    public AuditoriaPick save(AuditoriaPick auditoria) {
        var entity = new AuditoriaPickJpaEntity();
        entity.setIdAuditoria(auditoria.idAuditoria());
        entity.setFechaEvento(auditoria.fechaEvento());
        entity.setIdUsuario(auditoria.idUsuario());
        entity.setEntidadTab(auditoria.entidadTab());
        entity.setEntidadId(auditoria.entidadId());
        entity.setAccion(auditoria.accion());
        entity.setIpOrigen(auditoria.ipOrigen());
        entity.setMsOrigen(auditoria.msOrigen());
        entity.setValoresAnteriores(auditoria.valoresAnteriores());
        entity.setValoresNuevos(auditoria.valoresNuevos());

        repository.save(entity);

        return auditoria;
    }
    
}
