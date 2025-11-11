package rule.engine.org.app.domain.entity.ui;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum for fact type values
 * Represents different types of business entities that rules can be applied to
 */
public enum FactType {
    DECLARATION("Declaration"),
    CARGO_REPORT("CargoReport");
    
    private final String value;
    
    FactType(String value) {
        this.value = value;
    }
    
    /**
     * Get string value for JSON serialization
     */
    @JsonValue
    public String getValue() {
        return value;
    }
    
    /**
     * Convert string value to enum for JSON deserialization
     */
    @JsonCreator
    public static FactType fromValue(String value) {
        if (value == null) {
            return DECLARATION; // Default
        }
        for (FactType factType : values()) {
            if (factType.value.equalsIgnoreCase(value)) {
                return factType;
            }
        }
        return DECLARATION; // Default
    }
}

