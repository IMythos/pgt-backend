package com.portable.microservices.ms_tracking.picking.infrastructure.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.portable.microservices.ms_tracking.picking.infrastructure.client.dto.LocationApiResponse;
import com.portable.microservices.ms_tracking.picking.infrastructure.client.dto.LocationResponse;
import com.portable.microservices.ms_tracking.picking.infrastructure.client.dto.SalidaBatchFeignRequest;

@FeignClient(name = "ms-inventory")
public interface InventoryFeignClient {

    @GetMapping("/api/v1/locations")
    LocationApiResponse<List<LocationResponse>> getAllLocations();

    @PostMapping("/api/v1/movimientos/salida-picking")
    Map<String, Object> registrarSalidaBatch(@RequestBody SalidaBatchFeignRequest request);
}