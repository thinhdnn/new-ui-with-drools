package rule.engine.org.app.domain.entity.execution.cargo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rule.engine.org.app.domain.entity.common.BaseAuditableEntity;
import java.math.BigDecimal;

/**
 * Transport Equipment entity (Container, Trailer, etc.)
 * Part of Cargo Report
 */
@Entity
@Table(name = "transport_equipment")
@Data
@EqualsAndHashCode(callSuper = true)
public class TransportEquipment extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_report_id", nullable = false)
    private CargoReport cargoReport;

    @Column(name = "equipment_id")
    private String equipmentId; // Container number, trailer number, etc.

    @Column(name = "equipment_type_code")
    private String equipmentTypeCode; // "CN" = Container, "TR" = Trailer, etc.

    @Column(name = "seal_id")
    private String sealId; // Seal number

    @Column(name = "gross_mass_measure")
    private BigDecimal grossMassMeasure; // Gross weight of equipment with cargo
}

