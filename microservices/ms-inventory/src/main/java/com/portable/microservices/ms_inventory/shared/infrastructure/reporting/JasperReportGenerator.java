package com.portable.microservices.ms_inventory.shared.infrastructure.reporting;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class JasperReportGenerator {
    public Resource generatePdfReport(String reportPath, Map<String, Object> parameters, Collection<?> dataSource) {
        try {
            File file = ResourceUtils.getFile(reportPath);
            JasperReport jr = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource jrDataSource = new JRBeanCollectionDataSource(dataSource);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jr, parameters, jrDataSource);
            byte[] reportBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            return new ByteArrayResource(reportBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el reporte PDF: " + e.getMessage(), e);
        }
    }
}
