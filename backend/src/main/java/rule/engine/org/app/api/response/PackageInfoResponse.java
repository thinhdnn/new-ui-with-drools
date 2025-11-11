package rule.engine.org.app.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * DTO for package info response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackageInfoResponse {
    private Long version;
    private String releaseId;
    private String packageName;
    private String factType;
    private Integer rulesCount;
    private String rulesHash;
    private String changesDescription;
    private String ruleIds;
    private Instant deployedAt;
    private String deployedBy;
    private Map<String, Object> ruleChanges;
    private List<VersionHistoryItem> versionHistory;
    
    /**
     * Nested DTO for version history items
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VersionHistoryItem {
        private Long version;
        private Integer rulesCount;
        private String releaseId;
        private String changesDescription;
        private String ruleIds;
        private Instant deployedAt;
        private String deployedBy;
        private Map<String, Object> ruleChanges;
    }
}

