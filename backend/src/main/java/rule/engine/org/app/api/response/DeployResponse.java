package rule.engine.org.app.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for deploy rules response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeployResponse {
    private Long version;
    private String releaseId;
    private String message;
    private Integer rulesCount;
    private String changesDescription;
    private Instant deployedAt;
}

