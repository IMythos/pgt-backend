package com.portable.microservices.ms_tracking.picking.infrastructure.client.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LocationApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
}
