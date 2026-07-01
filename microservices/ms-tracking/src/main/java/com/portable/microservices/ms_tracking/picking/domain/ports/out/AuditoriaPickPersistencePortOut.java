package com.portable.microservices.ms_tracking.picking.domain.ports.out;

import com.portable.microservices.ms_tracking.picking.domain.model.AuditoriaPick;

public interface AuditoriaPickPersistencePortOut {
    AuditoriaPick save(AuditoriaPick auditoria);
}