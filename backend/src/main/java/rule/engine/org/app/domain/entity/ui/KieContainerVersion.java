package rule.engine.org.app.domain.entity.ui;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rule.engine.org.app.domain.entity.common.BaseAuditableEntity;

/**
 * KieContainerVersion - Tracks KieContainer versions and changes on each deployment
 * 
 * Each time rules are rebuilt (deploy), a new version is created with:
 * - Version number (auto-increments)
 * - Rules count and hash
 * - ReleaseId from Drools
 * - Description of changes
 * - List of rule IDs included
 */
@Entity
@Table(name = "kie_container_versions", indexes = {
    @Index(name = "idx_kie_container_versions_version", columnList = "version"),
    @Index(name = "idx_kie_container_versions_created_at", columnList = "created_date"),
    @Index(name = "idx_kie_container_versions_fact_type", columnList = "fact_type")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_kie_container_versions_fact_type_version", columnNames = {"fact_type", "version"})
})
@Data
@EqualsAndHashCode(callSuper = true)
public class KieContainerVersion extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fact type this container version applies to (e.g., "Declaration", "CargoReport")
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "fact_type", nullable = false, length = 100)
    private FactType factType = FactType.DECLARATION;

    /**
     * KieContainer version number (auto-increments on each deploy for this fact type)
     */
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * Number of rules in this container version
     */
    @Column(name = "rules_count", nullable = false)
    private Integer rulesCount;

    /**
     * MD5 hash of rules to detect changes
     */
    @Column(name = "rules_hash", nullable = false, length = 64)
    private String rulesHash;

    /**
     * Drools ReleaseId for this container version
     */
    @Column(name = "release_id", length = 255)
    private String releaseId;

    /**
     * Description of changes in this version
     */
    @Column(name = "changes_description", columnDefinition = "text")
    private String changesDescription;

    /**
     * Comma-separated list of rule IDs included in this version
     */
    @Column(name = "rule_ids", columnDefinition = "text")
    private String ruleIds;

    /**
     * JSON object containing detailed rule changes
     * Format: {"added": [ruleIds], "removed": [ruleIds], "updated": [ruleIds]}
     */
    @Column(name = "rule_changes_json", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String ruleChangesJson;
}

