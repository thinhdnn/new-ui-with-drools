package rule.engine.org.app.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for condition in response
 * Used when reconstructing conditions from RuleCondition entities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConditionResponse {
    private String field;
    private String operator;
    private Object value;
    private String logicalOp; // "AND" or "OR"
}

