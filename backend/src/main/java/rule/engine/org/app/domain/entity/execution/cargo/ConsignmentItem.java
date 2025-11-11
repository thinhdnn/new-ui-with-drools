package rule.engine.org.app.domain.entity.execution.cargo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rule.engine.org.app.domain.entity.common.BaseAuditableEntity;
import java.math.BigDecimal;

/**
 * Consignment Item entity (Line item within a consignment)
 * Part of Cargo Report
 */
@Entity
@Table(name = "consignment_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class ConsignmentItem extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consignment_id", nullable = false)
    private Consignment consignment;

    @Column(name = "sequence_numeric")
    private Integer sequenceNumeric; // Line number (1, 2, 3, ...)

    @Column(name = "goods_description", columnDefinition = "TEXT")
    private String goodsDescription; // Commercial description

    @Column(name = "hs_id")
    private String hsId; // HS code (e.g., "610910")

    @Column(name = "origin_country_id")
    private String originCountryId; // Country of origin

    // Quantity
    @Column(name = "quantity_quantity")
    private BigDecimal quantityQuantity; // Statistical quantity

    @Column(name = "quantity_unit_code")
    private String quantityUnitCode; // Unit code (e.g., "NMB", "KGM")

    // Weight
    @Column(name = "net_weight_measure")
    private BigDecimal netWeightMeasure;

    @Column(name = "gross_weight_measure")
    private BigDecimal grossWeightMeasure;
}

