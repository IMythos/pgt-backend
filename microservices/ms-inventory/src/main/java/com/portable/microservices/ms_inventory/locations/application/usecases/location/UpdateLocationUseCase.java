package com.portable.microservices.ms_inventory.locations.application.usecases.location;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.portable.microservices.ms_inventory.locations.domain.model.Location;
import com.portable.microservices.ms_inventory.locations.domain.ports.in.location.UpdateLocationPortIn;
import com.portable.microservices.ms_inventory.locations.domain.ports.out.LocationPersistencePortOut;


@Service
public class UpdateLocationUseCase implements UpdateLocationPortIn {

    private final LocationPersistencePortOut persistence;   

    public UpdateLocationUseCase(LocationPersistencePortOut persistence) {
        this.persistence = persistence;
    }


@Override
    public Location execute(UUID idLocacion, Location location) {
        Location existing = persistence.findById(idLocacion)
            .orElseThrow(() -> new RuntimeException("Locación no encontrada"));

        Location updated = existing.toBuilder()
            .zona(location.zona() != null && !location.zona().isBlank() ? location.zona() : existing.zona())
            .pasillo(location.pasillo() != null && !location.pasillo().isBlank() ? location.pasillo() : existing.pasillo())
            .estante(location.estante() != null && !location.estante().isBlank() ? location.estante() : existing.estante())
            .codBarras(location.codBarras() != null && !location.codBarras().isBlank() ? location.codBarras() : existing.codBarras())
            
            // Para números (Integer) solo comprobamos si es null
            .capacidad(location.capacidad() != null ? location.capacidad() : existing.capacidad())
            .posX(location.posX() != null ? location.posX() : existing.posX())
            .posY(location.posY() != null ? location.posY() : existing.posY())
            .build();

        return persistence.save(updated);
    }
       

       
}

