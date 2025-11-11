package rule.engine.org.app.domain.entity.execution.declaration;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rule.engine.org.app.domain.entity.common.BaseAuditableEntity;
import java.math.BigDecimal;

/**
 * Government Agency Goods Item (Line Item) following WCO Data Model 3.9
 */
@Entity
@Table(name = "government_agency_goods_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class GovernmentAgencyGoodsItem extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "declaration_id", nullable = false)
    private Declaration declaration;

    @Column(name = "sequence_numeric")
    private Integer sequenceNumeric; // Line number (1, 2, 3, ...)

    // HS Code and Description
    @Column(name = "hs_id")
    private String hsId; // HS code (e.g., "610910")

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Commercial description

    // Origin
    @Column(name = "origin_country_id")
    private String originCountryId; // Country of origin

    // Weight and Quantity
    @Column(name = "net_weight_measure")
    private BigDecimal netWeightMeasure;

    @Column(name = "gross_weight_measure")
    private BigDecimal grossWeightMeasure;

    @Column(name = "quantity_quantity")
    private BigDecimal quantityQuantity; // Statistical quantity

    @Column(name = "quantity_unit_code")
    private String quantityUnitCode; // Unit code (e.g., "NMB", "KGM")

    // Invoice line
    @Column(name = "invoice_line_number_id")
    private String invoiceLineNumberId;

    @Column(name = "unit_price_amount")
    private BigDecimal unitPriceAmount;

    // Value
    @Column(name = "statistical_value_amount")
    private BigDecimal statisticalValueAmount;

    @Column(name = "customs_value_amount")
    private BigDecimal customsValueAmount;

    // Procedure and Preference
    @Column(name = "procedure_code")
    private String procedureCode; // e.g., "4000" = Free circulation

    @Column(name = "previous_procedure_code")
    private String previousProcedureCode;

    @Column(name = "preference_code")
    private String preferenceCode; // Preference treatment (e.g., "000", "100")

    // Valuation
    @Column(name = "valuation_method_code")
    private String valuationMethodCode; // "1" to "6" per WTO Valuation Agreement

    // Duty
    @Column(name = "duty_rate")
    private BigDecimal dutyRate; // Duty percentage (e.g., 12.0 = 12%)

    @Column(name = "duty_amount")
    private BigDecimal dutyAmount; // Calculated duty amount
}

