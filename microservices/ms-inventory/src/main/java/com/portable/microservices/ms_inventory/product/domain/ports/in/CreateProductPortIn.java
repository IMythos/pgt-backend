package com.portable.microservices.ms_inventory.product.domain.ports.in;

import com.portable.microservices.ms_inventory.product.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CreateProductPortIn {
    Product execute(CreateProductCommand command);

    record CreateProductCommand(
        Long id_categoria,
        Long id_marca,
        String cod_prod,
        String cod_anexo,
        String descripcion,
        Map<String, Object> modelos_compatibles,
        BigDecimal pre_com,
        BigDecimal pre_ven,
        Integer stock_minimo,
        Integer stock_inicial,
        UUID idLocacion
    ) {}
}