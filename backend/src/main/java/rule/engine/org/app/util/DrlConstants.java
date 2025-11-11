package rule.engine.org.app.util;

/**
 * Constants for Drools Rule Language (DRL) generation
 */
public class DrlConstants {
    
    // Package name for DRL rules
    public static final String DRL_PACKAGE = "rules";
    
    // Required imports for DRL rules
    public static final String[] DRL_IMPORTS = {
        "rule.engine.org.app.domain.entity.execution.declaration.Declaration",
        "rule.engine.org.app.domain.entity.execution.declaration.GovernmentAgencyGoodsItem",
        "rule.engine.org.app.domain.entity.execution.cargo.CargoReport",
        "rule.engine.org.app.domain.entity.execution.cargo.TransportEquipment",
        "rule.engine.org.app.domain.entity.execution.cargo.Consignment",
        "rule.engine.org.app.domain.entity.execution.cargo.ConsignmentItem",
        "rule.engine.org.app.domain.entity.execution.RuleOutputHit",
        "rule.engine.org.app.domain.entity.execution.TotalRuleResults",
        "java.math.BigDecimal"
    };
    
    // Global declarations for DRL rules
    public static final String[] DRL_GLOBALS = {
        "TotalRuleResults totalResults"
    };
    
    /**
     * Build DRL header (package, imports, globals)
     */
    public static String buildDrlHeader() {
        StringBuilder header = new StringBuilder();
        
        // Package declaration
        header.append("package ").append(DRL_PACKAGE).append("\n\n");
        
        // Imports
        for (String importClass : DRL_IMPORTS) {
            header.append("import ").append(importClass).append("\n");
        }
        header.append("\n");
        
        // Global declarations
        for (String global : DRL_GLOBALS) {
            header.append("global ").append(global).append("\n");
        }
        header.append("\n");
        
        return header.toString();
    }
    
    private DrlConstants() {
        // Utility class - prevent instantiation
    }
}

