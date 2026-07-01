package com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_pick", schema = "tracking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePickJpaEntity {
    @Id
    @Column(name = "id_detalle")
    private UUID idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden", nullable = false)
    private OrdenPickJpaEntity orden;

    @Column(name = "producto_id", nullable = false)
    private UUID productoId;

    @Column(name = "locacion_id", nullable = false)
    private UUID locacionId;

    @Column(name = "cant_requerida", nullable = false)
    private Integer cantRequerida;

    @Column(name = "cant_seleccion", nullable = false)
    private Integer cantSeleccion;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;
}
