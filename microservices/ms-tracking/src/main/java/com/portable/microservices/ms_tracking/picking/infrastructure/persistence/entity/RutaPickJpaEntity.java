package com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rutas_pick", schema = "tracking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RutaPickJpaEntity {
    @Id
    @Column(name = "id_ruta")
    private UUID idRuta;

    @Column(name = "id_orden", nullable = false, unique = true)
    private UUID idOrden;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "path_seq", columnDefinition = "jsonb", nullable = false)
    private List<UUID> pathSeq;

    @Column(name = "distancia_estimada", precision = 10, scale = 2)
    private BigDecimal distanciaEstimada;

    @Column(name = "fec_creacion")
    private OffsetDateTime fecCreacion;
}
