package rule.engine.org.app.domain.entity.execution.cargo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rule.engine.org.app.domain.entity.common.BaseAuditableEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Consignment entity (House Bill of Lading level)
 * Part of Cargo Report
 */
@Entity
@Table(name = "consignments")
@Data
@EqualsAndHashCode(callSuper = true)
public class Consignment extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_report_id", nullable = false)
    private CargoReport cargoReport;

    @Column(name = "transport_contract_document_id")
    private String transportContractDocumentId; // House Bill of Lading number

    @Column(name = "ucr")
    private String ucr; // Unique Consignment Reference

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

    // Notify Party
    @Column(name = "notify_party_id")
    private String notifyPartyId;

    @Column(name = "notify_party_name")
    private String notifyPartyName;

    @Column(name = "notify_party_country_id")
    private String notifyPartyCountryId;

    // Package information
    @Column(name = "marks_numbers_id")
    private String marksNumbersId; // Shipping marks

    @Column(name = "package_quantity")
    private Integer packageQuantity;

    @Column(name = "package_type_code")
    private String packageTypeCode; // "CT" = Carton, "PK" = Package, etc.

    @Column(name = "gross_mass_measure")
    private BigDecimal grossMassMeasure;

    // Location
    @Column(name = "loading_location_id")
    private String loadingLocationId;

    @Column(name = "unloading_location_id")
    private String unloadingLocationId;

    // Relationship to consignment items
    @OneToMany(mappedBy = "consignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsignmentItem> consignmentItems = new ArrayList<>();
}

