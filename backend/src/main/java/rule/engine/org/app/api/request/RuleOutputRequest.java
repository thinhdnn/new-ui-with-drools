package rule.engine.org.app.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for rule output request body
 * Used when creating or updating rule outputs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleOutputRequest {
    private String action;
    private String result;
    private BigDecimal score;
    private String flag;
    private String documentType;
    private String documentId;
    private String description;
}

