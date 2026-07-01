package com.portable.microservices.ms_tracking.picking.infrastructure.persistence.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auditoria_pick", schema = "tracking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaPickJpaEntity {
    @Id
    @Column(name = "id_auditoria")
    private UUID idAuditoria;

    @Column(name = "fecha_evento")
    private OffsetDateTime fechaEvento;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "entidad_tab", length = 100)
    private String entidadTab;

    @Column(name = "entidad_id", length = 100)
    private String entidadId;

    @Column(name = "accion", length = 50)
    private String accion;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "ms_origen", length = 50)
    private String msOrigen;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valores_anteriores", columnDefinition = "jsonb")
    private String valoresAnteriores;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valores_nuevos", columnDefinition = "jsonb")
    private String valoresNuevos;
}
