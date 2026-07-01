package com.portable.microservices.ms_tracking.picking.application.usecases;

import java.util.List;

import org.springframework.stereotype.Service;

import com.portable.microservices.ms_tracking.picking.domain.model.OrdenPick;
import com.portable.microservices.ms_tracking.picking.domain.ports.in.ListarOrdenesPickPortIn;
import com.portable.microservices.ms_tracking.picking.domain.ports.out.OrdenPickPersistencePortOut;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarOrdenesPickUseCase implements ListarOrdenesPickPortIn {
    private final OrdenPickPersistencePortOut ordenPickPersistence;

    @Override
    public List<OrdenPick> execute() {
        return ordenPickPersistence.findAll();
    }

    @Override
    public List<OrdenPick> executeByEstado(String estado) {
        return ordenPickPersistence.findByEstado(estado);
    }
    
}
