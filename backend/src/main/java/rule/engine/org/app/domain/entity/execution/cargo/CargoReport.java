package rule.engine.org.app.domain.entity.execution.cargo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rule.engine.org.app.domain.entity.common.BaseAuditableEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cargo Report entity following WCO Data Model 3.9
 * Represents a cargo manifest/report with transport means and consignments
 */
@Entity
@Table(name = "cargo_reports")
@Data
@EqualsAndHashCode(callSuper = true)
public class CargoReport extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Core identification
    @Column(name = "function_code")
    private String functionCode; // "1" = original, "9" = cancel, etc.

    @Column(name = "type_code")
    private String typeCode; // "CRI" = Cargo Report Import, "CRE" = Cargo Report Export

    @Column(name = "office_id")
    private String officeId; // e.g., "VNHPH"

    @Column(name = "report_id", unique = true)
    private String reportId; // e.g., "MANIFEST-2025-0001"

    @Column(name = "submission_date_time")
    private LocalDateTime submissionDateTime;

    // Transport means
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

    @Column(name = "estimated_departure_date_time")
    private LocalDateTime estimatedDepartureDateTime;

    @Column(name = "estimated_arrival_date_time")
    private LocalDateTime estimatedArrivalDateTime;

    // Carrier
    @Column(name = "carrier_id")
    private String carrierId;

    @Column(name = "carrier_name")
    private String carrierName;

    @Column(name = "carrier_country_id")
    private String carrierCountryId;

    // Master transport document
    @Column(name = "master_transport_document_id")
    private String masterTransportDocumentId; // Master Bill of Lading

    // Relationship to transport equipment
    @OneToMany(mappedBy = "cargoReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransportEquipment> transportEquipment = new ArrayList<>();

    // Relationship to consignments
    @OneToMany(mappedBy = "cargoReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consignment> consignments = new ArrayList<>();
}

