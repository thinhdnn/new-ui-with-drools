package rule.engine.org.app.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for optional fields in rule request body
 * Used when building RuleResponse to include request fields (description, conditions, output)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleRequestFields {
    private String description;
    private List<Map<String, Object>> conditions;
    private Map<String, Object> output;
}

