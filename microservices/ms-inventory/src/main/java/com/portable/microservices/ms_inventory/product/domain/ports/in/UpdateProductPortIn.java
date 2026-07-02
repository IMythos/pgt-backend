package com.portable.microservices.ms_inventory.product.domain.ports.in;

import com.portable.microservices.ms_inventory.product.domain.model.Product;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UpdateProductPortIn {
    Product execute(UUID id, UpdateProductCommand command);

    record UpdateProductCommand(
        Long id_categoria,
        Long id_marca,
        String cod_prod,
        String cod_anexo,
        String descripcion,
        Map<String, Object> modelosCompatibles,
        Boolean estado
    ) {}
}