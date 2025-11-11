package rule.engine.org.app.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for container status response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerStatusResponse {

    private List<ContainerStatus> containers;
    private Integer totalContainers;
    private List<String> factTypes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContainerStatus {
        private String factType;
        private Boolean exists;
        private Boolean valid;
        private Long version;
        private String releaseId;
        private String rulesHash;
        private Integer ruleCount;
        private String message;
        private String error;
    }
}
