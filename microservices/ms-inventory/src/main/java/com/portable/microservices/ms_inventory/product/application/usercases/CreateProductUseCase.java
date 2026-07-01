package com.portable.microservices.ms_inventory.product.application.usercases;

import com.portable.microservices.ms_inventory.kardex.domain.ports.out.LotPersistencePortOut;
import com.portable.microservices.ms_inventory.movement.domain.ports.in.RegisterIngresoPortIn;
import com.portable.microservices.ms_inventory.product.domain.model.Product;
import com.portable.microservices.ms_inventory.product.domain.ports.in.CreateProductPortIn;
import com.portable.microservices.ms_inventory.product.domain.ports.out.ProductPersistencePortOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateProductUseCase implements CreateProductPortIn {

    private final ProductPersistencePortOut productPersistence;
    private final RegisterIngresoPortIn registerIngresoUseCase;

    @Override
    @Transactional
    public Product execute(CreateProductCommand command) {
        if (productPersistence.findAll().stream()
                .anyMatch(p -> p.codProd().equals(command.cod_prod()))) {
            throw new IllegalArgumentException("Ya existe un producto con el código: " + command.cod_prod());
        }

        Product nuevo = new Product(
            UUID.randomUUID(),
            command.id_categoria(),
            command.id_marca(),
            command.cod_prod(),
            command.cod_anexo(),
            command.descripcion(),
            command.modelos_compatibles(),
            command.pre_com(),
            command.pre_ven(),
            true,
            ZonedDateTime.now(),
            command.stock_minimo(),
            0
        );

        Product guardado = productPersistence.save(nuevo);

        // Si se indicó stock inicial, registrar ingreso atómico (lote + movimiento + kardex)
        if (command.stock_inicial() != null && command.stock_inicial() > 0) {
            // Usar costo unitario del producto (pre_com) como costo de ingreso
            BigDecimal costoUnit = command.pre_com() != null ? command.pre_com() : BigDecimal.ONE;
            registerIngresoUseCase.execute(
                new RegisterIngresoPortIn.RegisterIngresoCommand(
                    guardado.id(),
                    command.idLocacion(),
                    "INICIAL",
                    null,
                    costoUnit,
                    command.stock_inicial(),
                    1L,
                    "Stock inicial al crear producto",
                    null,
                    null,
                    null,
                    null
                )
            );
            guardado = productPersistence.findById(guardado.id())
                    .orElse(guardado);
        }

        return guardado;
    }
}