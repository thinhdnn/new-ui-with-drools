package rule.engine.org.app.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rule.engine.org.app.domain.entity.ui.DecisionRule;

/**
 * Response DTO for rule detail endpoint
 * Includes both the rule data and Declaration field metadata
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleDetailResponse {
    // The rule itself
    private DecisionRule rule;
    
    // Declaration field metadata for building/editing rules
    private RuleFieldMetadata metadata;
}

