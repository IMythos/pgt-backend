package com.portable.microservices.ms_inventory.product.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record CreateProductRequest(
    @NotNull Long categoryId,
    @NotNull Long brandId,
    @NotBlank @Size(max = 30) String codProd,
    @Size(max = 30) String codAnexo,
    @NotBlank String descripcion,
    Map<String, Object> modelosCompatibles,
    @NotNull BigDecimal preCom,
    @NotNull BigDecimal preVen,
    Integer stockMinimo,
    Integer stockInicial,
    String idLocacion
) {}
