package com.portable.microservices.ms_inventory.product.infrastructure.persistence.adapter.out.reporting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.portable.microservices.ms_inventory.product.domain.model.Product;
import com.portable.microservices.ms_inventory.shared.application.service.ExcelGeneratorService;
import com.portable.microservices.ms_inventory.shared.domain.ports.out.ReportPortOut;
import com.portable.microservices.ms_inventory.shared.infrastructure.reporting.JasperReportGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductJasperAdapter implements ReportPortOut {
    private final JasperReportGenerator jasperReportGenerator;
    private final ExcelGeneratorService excelGenerator;

    @Override
    public Resource exportProductsReportToPdf(List<Product> products) {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("REPORTE_PRODUCTOS", "Reporte de Inventario de Productos");
        parameters.put("FECHA_GENERACION", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return jasperReportGenerator.generatePdfReport(
            "classpath:reports/productos_reporte.jrxml", 
            parameters, 
            products
        );
    }

    @Override
    public Resource exportProductsReportToExcel(List<Product> products) {
        return excelGenerator.generateProductExcel(products);
    }
}
