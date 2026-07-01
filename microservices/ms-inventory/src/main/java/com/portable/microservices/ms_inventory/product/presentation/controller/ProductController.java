package com.portable.microservices.ms_inventory.product.presentation.controller;

import com.portable.microservices.ms_inventory.product.application.usercases.CreateProductUseCase;
import com.portable.microservices.ms_inventory.product.application.usercases.FindProductUseCase;
import com.portable.microservices.ms_inventory.product.application.usercases.UpdateProductUseCase;
import com.portable.microservices.ms_inventory.product.application.usercases.DeleteProductUseCase;
import com.portable.microservices.ms_inventory.product.application.usercases.ExportProductUseCase;
import com.portable.microservices.ms_inventory.product.domain.model.Product;
import com.portable.microservices.ms_inventory.product.domain.ports.in.ExportProductPortIn.ExportFormat;
import com.portable.microservices.ms_inventory.product.presentation.dto.CreateProductRequest;
import com.portable.microservices.ms_inventory.product.presentation.dto.ProductResponse;
import com.portable.microservices.ms_inventory.product.presentation.mapper.ProductPresentationMapper;
import com.portable.shared.infrastructure.presentation.PagedResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final FindProductUseCase findProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final ProductPresentationMapper presentationMapper;
    private final ExportProductUseCase exportProductUseCase;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        System.out.println("TIPO DE DATO RECIBIDO: " + request.modelosCompatibles().getClass().getName());
        
        Product product = createProductUseCase.execute(
            new CreateProductUseCase.CreateProductCommand(
                request.categoryId(),
                request.brandId(),
                request.codProd(),
                request.codAnexo(),
                request.descripcion(),
                request.modelosCompatibles(),
                request.preCom(),
                request.preVen(),
                request.stockMinimo(),
                request.stockInicial(),
                request.idLocacion() != null ? UUID.fromString(request.idLocacion()) : null
            )
        );
        return ResponseEntity.ok(presentationMapper.toResponse(product));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> findAll(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "50") int tamanioPagina) {
        PagedResponse<Product> page = findProductUseCase.findAll(texto, idCategoria, estado, pagina, tamanioPagina);
        List<ProductResponse> items = page.items().stream()
                .map(presentationMapper::toResponse)
                .toList();
        return ResponseEntity.ok(new PagedResponse<>(items, page.total(), page.page(), page.pageSize()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable UUID id) {
        return findProductUseCase.findById(id)
                .map(p -> ResponseEntity.ok(presentationMapper.toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id,
            @RequestBody UpdateProductUseCase.UpdateProductCommand command) {
        Product updated = updateProductUseCase.execute(id, command);
        return ResponseEntity.ok(presentationMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteProductUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(findProductUseCase.count());
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportToExcel(@RequestParam(defaultValue = "EXCEL") String format) {
        ExportFormat exportFormat;
        try {
            exportFormat = ExportFormat.valueOf(format.toUpperCase());
        } catch (IllegalArgumentException e) {
            exportFormat = ExportFormat.EXCEL;
        }
        Resource resource = exportProductUseCase.exportToFormat(exportFormat);
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String extension = exportFormat == ExportFormat.PDF ? "pdf" : "xlsx";
        String filename = "inventario-productos-" + fecha + "." + extension;

        String mediaType = exportFormat == ExportFormat.PDF
                ? "application/pdf"
                : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(mediaType))
                .body(resource);
    }
}
