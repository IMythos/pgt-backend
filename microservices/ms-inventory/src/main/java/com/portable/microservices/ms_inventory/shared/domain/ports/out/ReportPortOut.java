package com.portable.microservices.ms_inventory.shared.domain.ports.out;

import java.util.List;

import org.springframework.core.io.Resource;

import com.portable.microservices.ms_inventory.product.domain.model.Product;

public interface ReportPortOut {
    Resource exportProductsReportToPdf(List<Product> products);
    Resource exportProductsReportToExcel(List<Product> products);
}
