package rule.engine.org.app.domain.entity.execution.declaration;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rule.engine.org.app.domain.entity.common.BaseAuditableEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Declaration entity following WCO Data Model 3.9
 * Single flat structure with nested GovernmentAgencyGoodsItem
 */
@Entity
@Table(name = "declarations")
@Data
@EqualsAndHashCode(callSuper = true)
public class Declaration extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Core identification
    @Column(name = "function_code")
    private String functionCode; // "1" = original, "9" = cancel, etc.

    @Column(name = "type_code")
    private String typeCode; // "IM" = Import, "EX" = Export

    @Column(name = "office_id")
    private String officeId; // e.g., "VNHPH"

    @Column(name = "declaration_id", unique = true)
    private String declarationId; // e.g., "23IM123456"

    @Column(name = "submission_date_time")
    private LocalDateTime submissionDateTime;

    @Column(name = "ucr")
    private String ucr; // Unique Consignment Reference

    // Declarant (Broker/Agent)
    @Column(name = "declarant_id")
    private String declarantId;

    @Column(name = "declarant_name")
    private String declarantName;

    @Column(name = "declarant_country_id")
    private String declarantCountryId;

    // Consignor (Seller/Exporter)
    @Column(name = "consignor_id")
    private String consignorId;

    @Column(name = "consignor_name")
    private String consignorName;

    @Column(name = "consignor_country_id")
    private String consignorCountryId;

    // Consignee (Buyer/Importer at delivery)
    @Column(name = "consignee_id")
    private String consigneeId;

    @Column(name = "consignee_name")
    private String consigneeName;

    @Column(name = "consignee_country_id")
    private String consigneeCountryId;

    // Importer (Legal entity for customs)
    @Column(name = "importer_id")
    private String importerId;

    @Column(name = "importer_name")
    private String importerName;

    @Column(name = "importer_country_id")
    private String importerCountryId;

    // Country information
    @Column(name = "country_of_export_id")
    private String countryOfExportId;

    @Column(name = "country_of_import_id")
    private String countryOfImportId;

    @Column(name = "country_of_destination_id")
    private String countryOfDestinationId;

    // Incoterm
    @Column(name = "incoterm_code")
    private String incotermCode; // "CIF", "FOB", etc.

    // Invoice
    @Column(name = "invoice_id")
    private String invoiceId;

    @Column(name = "invoice_issue_date_time")
    private LocalDateTime invoiceIssueDateTime;

    @Column(name = "invoice_currency_code")
    private String invoiceCurrencyCode;

    @Column(name = "invoice_amount")
    private BigDecimal invoiceAmount;

    // Transport
    @Column(name = "transport_means_mode_code")
    private String transportMeansModeCode; // "1" = Maritime, "2" = Rail, "3" = Road, "4" = Air

    @Column(name = "transport_means_id")
    private String transportMeansId; // IMO, Flight number, etc.

    @Column(name = "transport_means_journey_id")
    private String transportMeansJourneyId; // Voyage/Flight/Trip number

    // Location
    @Column(name = "loading_location_id")
    private String loadingLocationId; // Port of loading (e.g., "CNSHA")

    @Column(name = "unloading_location_id")
    private String unloadingLocationId; // Port of discharge (e.g., "VNHPH")

    @Column(name = "location_of_goods_id")
    private String locationOfGoodsId;

    @Column(name = "warehouse_id")
    private String warehouseId;

    // Package
    @Column(name = "package_quantity")
    private Integer packageQuantity;

    @Column(name = "marks_numbers_id")
    private String marksNumbersId; // Shipping marks

    // Totals
    @Column(name = "total_gross_mass_measure")
    private BigDecimal totalGrossMassMeasure;

    @Column(name = "total_net_mass_measure")
    private BigDecimal totalNetMassMeasure;

    @Column(name = "total_freight_amount")
    private BigDecimal totalFreightAmount;

    @Column(name = "total_insurance_amount")
    private BigDecimal totalInsuranceAmount;

    @Column(name = "other_charges_amount")
    private BigDecimal otherChargesAmount;

    // Previous documents (stored as JSON array string)
    @Column(name = "previous_document_ids", columnDefinition = "TEXT")
    private String previousDocumentIds; // e.g., '["BL:OOLU1234567890"]'

    // Relationship to goods items (used by Drools; ignore missing rows at runtime)
    @OneToMany(mappedBy = "declaration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GovernmentAgencyGoodsItem> governmentAgencyGoodsItems = new ArrayList<>();
}

