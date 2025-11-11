package rule.engine.org.app.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleFieldMetadata {
    private List<FieldDefinition> inputFields;  // For WHEN conditions
    private List<FieldDefinition> outputFields; // For THEN actions
    private Map<String, List<OperatorDefinition>> operatorsByType; // Operators grouped by field type
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldDefinition {
        private String name;
        private String label;
        private String type; // "string", "number", "decimal", "integer", "boolean"
        private String description;
        private Integer orderIndex; // Order for UI display (optional, defaults to insertion order)
        
        // Constructor without orderIndex for backward compatibility
        public FieldDefinition(String name, String label, String type, String description) {
            this.name = name;
            this.label = label;
            this.type = type;
            this.description = description;
            this.orderIndex = null; // Will use insertion order
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperatorDefinition {
        private String operator;     // The actual operator symbol/code (e.g., "==", ">=", "contains")
        private String label;        // Human-readable label (e.g., "Equal to", "Greater than or equal to")
        private String description;  // Optional description
    }
}

