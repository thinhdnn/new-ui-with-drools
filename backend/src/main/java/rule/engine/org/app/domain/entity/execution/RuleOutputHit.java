package rule.engine.org.app.domain.entity.execution;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Runtime rule output hit (execution-only, not persisted).
 * This is created when a rule matches during Drools execution.
 */
@Data
@NoArgsConstructor
public class RuleOutputHit {

    /**
     * Action to take when this output is hit. Examples: FLAG, APPROVE, REJECT, REVIEW, HOLD.
     */
    private String action;

    /**
     * Result message/description.
     */
    private String result;

    /**
     * Risk score contributed by this output.
     */
    private BigDecimal score;

    /**
     * Optional flag label/category computed by the rule.
     */
    private String flag;

    /**
     * Optional related document type (e.g., INVOICE, LICENSE).
     */
    private String documentType;

    /**
     * Optional related document identifier.
     */
    private String documentId;

    /**
     * Optional longer description for reporting.
     */
    private String description;
}


