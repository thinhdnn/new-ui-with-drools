package rule.engine.org.app.domain.entity.ui;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rule.engine.org.app.domain.entity.common.BaseAuditableEntity;

import java.time.Instant;

/**
 * ChangeRequest - Stores change requests for rule modifications that require approval
 * Each change request is associated with a fact type and contains proposed changes
 */
@Entity
@Table(name = "change_requests")
@Data
@EqualsAndHashCode(callSuper = true)
public class ChangeRequest extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fact type this change request applies to (e.g., "Declaration", "CargoReport")
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "fact_type", nullable = false, length = 100)
    private FactType factType = FactType.DECLARATION;

    /**
     * Title/summary of the change request
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Detailed description of the proposed changes
     */
    @Column(name = "description", columnDefinition = "text")
    private String description;

    /**
     * Status of the change request: Pending, Approved, Rejected
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ChangeRequestStatus status = ChangeRequestStatus.PENDING;

    /**
     * JSON object containing proposed changes
     * Format: {"rulesToAdd": [ruleIds], "rulesToUpdate": [ruleIds], "rulesToDelete": [ruleIds]}
     */
    @Column(name = "changes_json", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String changesJson;

    /**
     * User who approved the change request
     */
    @Column(name = "approved_by")
    private String approvedBy;

    /**
     * Date when the change request was approved
     */
    @Column(name = "approved_date")
    private Instant approvedDate;

    /**
     * User who rejected the change request
     */
    @Column(name = "rejected_by")
    private String rejectedBy;

    /**
     * Date when the change request was rejected
     */
    @Column(name = "rejected_date")
    private Instant rejectedDate;

    /**
     * Reason for rejection if the change request was rejected
     */
    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason;
}

