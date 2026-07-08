package com.portable.microservices.ms_inventory.product.domain.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record Product(
    UUID id,
    Long categoryId,
    Long brandId,
    String codProd,
    String codAnexo,
    String descripcion,
    List<String> modelosCompatibles,
    BigDecimal preCom,
    BigDecimal preVen,
    boolean estado,
    ZonedDateTime fecCreacion,
    Integer stockMinimo,
    Integer stockTotal
) {
    public Product {
        stockMinimo = stockMinimo != null ? stockMinimo : 0;
        stockTotal = stockTotal != null ? stockTotal : 0;
    }
}