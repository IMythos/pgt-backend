package com.portable.microservices.ms_tracking.picking.infrastructure.client.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class SalidaBatchFeignRequest {
    private List<SalidaBatchItem> items;
    private String motivo;
    private String documentoRef;

    @Data
    public static class SalidaBatchItem {
        private UUID idLote;
        private Integer cantidad;
    }
}
