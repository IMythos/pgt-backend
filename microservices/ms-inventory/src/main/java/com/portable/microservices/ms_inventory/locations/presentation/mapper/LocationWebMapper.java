package com.portable.microservices.ms_inventory.locations.presentation.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.portable.microservices.ms_inventory.locations.domain.model.Location;
import com.portable.microservices.ms_inventory.locations.presentation.dto.CreateLocationRequest;
import com.portable.microservices.ms_inventory.locations.presentation.dto.LocationResponse;
import com.portable.microservices.ms_inventory.locations.presentation.dto.UpdateLocationRequest;

@Component
public class LocationWebMapper {

    public Location toDomain(CreateLocationRequest request) {
        if (request == null) return null;
        return Location.builder()
                .idAlmacen(request.idAlmacen())
                .zona(request.zona())
                .pasillo(request.pasillo())
                .estante(request.estante())
                .codBarras(request.codBarras())
                .capacidad(request.capacidad())
                .posX(request.posX())
                .posY(request.posY())
                .activo(true)
                .build();
    }

    public Location toDomain(UpdateLocationRequest request) {
        if (request == null) return null;
        return Location.builder()
                .zona(request.zona())
                .pasillo(request.pasillo())
                .estante(request.estante())
                .capacidad(request.capacidad())
                .posX(request.posX())
                .posY(request.posY())
                .build();
    }

    public LocationResponse toResponse(Location location) {
        if (location == null) return null;
        return new LocationResponse(
                location.idLocacion(),
                location.idAlmacen(),
                location.zona(),
                location.pasillo(),
                location.estante(),
                location.codBarras(),
                location.capacidad(),
                location.posX(),
                location.posY(),
                location.activo()
        );
    }

    public List<LocationResponse> toResponseList(List<Location> locations) {
        if (locations == null) return null;
        return locations.stream()
                .map(this::toResponse)
                .toList();
    }
}
