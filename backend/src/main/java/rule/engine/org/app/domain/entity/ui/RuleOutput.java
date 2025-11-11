package rule.engine.org.app.domain.entity.ui;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rule.engine.org.app.domain.entity.common.BaseAuditableEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "rule_output")
@Data
@EqualsAndHashCode(callSuper = true)
public class RuleOutput extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to output group (required by schema)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private RuleOutputGroup group;
    
    // Link directly to decision rule (for convenience)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_rule_id", nullable = true)
    private DecisionRule decisionRule;

    // Execution-aligned fields (match runtime RuleOutputHit)
    @Column(name = "action", length = 50)
    private String action;

    @Column(name = "result", columnDefinition = "text")
    private String result;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "flag")
    private String flag;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;
}


