package com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orden_pick", schema = "tracking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenPickJpaEntity {
    @Id
    @Column(name = "id_orden")
    private UUID idOrden;

    @Column(name = "usuario_picking_id", nullable = false)
    private Long usuarioPickingId;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "fec_creacion")
    private OffsetDateTime fecCreacion;

    @Column(name = "fec_inicio")
    private OffsetDateTime fecInicio;

    @Column(name = "fec_fin")
    private OffsetDateTime fecFin;

    @Column(name = "tipo_salida", length = 30)
    @Builder.Default
    private String tipoSalida = "SALIDA";

    @Column(name = "doc_ref", length = 100)
    private String docRef;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DetallePickJpaEntity> detalles = new ArrayList<>();
}
