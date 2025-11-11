package rule.engine.org.app.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rule.engine.org.app.domain.entity.execution.RuleOutputHit;
import rule.engine.org.app.domain.entity.execution.TotalRuleResults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for rule execution endpoint response
 * Encapsulates the result of executing rules against a declaration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleExecuteResponse {
    
    private Boolean success;
    private String declarationId;
    private LocalDateTime runAt;
    private BigDecimal totalScore;
    private String finalAction;
    private String finalFlag;
    private Integer hitsCount;
    private List<RuleOutputHitDto> hits;
    
    /**
     * Nested DTO for rule output hits
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RuleOutputHitDto {
        private String action;
        private BigDecimal score;
        private String result;
        private String flag;
        private String documentType;
        private String documentId;
        private String description;
        
        /**
         * Convert from RuleOutputHit entity
         */
        public static RuleOutputHitDto from(RuleOutputHit hit) {
            return RuleOutputHitDto.builder()
                .action(hit.getAction())
                .score(hit.getScore())
                .result(hit.getResult())
                .flag(hit.getFlag())
                .documentType(hit.getDocumentType())
                .documentId(hit.getDocumentId())
                .description(hit.getDescription())
                .build();
        }
    }
    
    /**
     * Factory method to create response from TotalRuleResults
     */
    public static RuleExecuteResponse from(TotalRuleResults results, String declarationId) {
        List<RuleOutputHitDto> hitsDto = null;
        if (results.getHits() != null && !results.getHits().isEmpty()) {
            hitsDto = results.getHits().stream()
                .map(RuleOutputHitDto::from)
                .collect(Collectors.toList());
        }
        
        return RuleExecuteResponse.builder()
            .success(true)
            .declarationId(declarationId)
            .runAt(results.getRunAt())
            .totalScore(results.getTotalScore())
            .finalAction(results.getFinalAction())
            .finalFlag(results.getFinalFlag())
            .hitsCount(results.getHits() != null ? results.getHits().size() : 0)
            .hits(hitsDto)
            .build();
    }
    
    /**
     * Factory method to create error response
     */
    public static RuleExecuteResponse error(String error, String errorType) {
        return RuleExecuteResponse.builder()
            .success(false)
            .build();
    }
}

