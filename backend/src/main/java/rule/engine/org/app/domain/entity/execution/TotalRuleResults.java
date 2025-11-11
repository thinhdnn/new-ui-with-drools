package rule.engine.org.app.domain.entity.execution;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregated execution result for a declaration: includes all hit outputs and final summary.
 * Execution-only, not persisted.
 */
@Data
@NoArgsConstructor
public class TotalRuleResults {

    /**
     * List of hit outputs produced by matched rules.
     */
    private List<RuleOutputHit> hits = new ArrayList<>();

    /**
     * Total score (e.g., sum of hit scores) after the run.
     */
    private BigDecimal totalScore;

    /**
     * Final flag/category determined by aggregation logic.
     */
    private String finalFlag;

    /**
     * Final action determined by aggregation logic (e.g., APPROVE/REVIEW/REJECT).
     */
    private String finalAction;

    /**
     * Execution timestamp.
     */
    private LocalDateTime runAt;
}


