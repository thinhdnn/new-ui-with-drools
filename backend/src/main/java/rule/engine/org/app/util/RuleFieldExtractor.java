package rule.engine.org.app.util;

import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import rule.engine.org.app.api.response.RuleFieldMetadata.FieldDefinition;
import rule.engine.org.app.api.response.RuleFieldMetadata.OperatorDefinition;
import rule.engine.org.app.domain.entity.execution.declaration.Declaration;
import rule.engine.org.app.domain.entity.execution.cargo.CargoReport;
import rule.engine.org.app.domain.entity.execution.RuleOutputHit;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleFieldExtractor {

    /**
     * Automatically extract all input fields from entity using reflection based on fact type.
     * @param factType Fact type (e.g., "Declaration", "CargoReport")
     * @return List of field definitions
     */
    public static List<FieldDefinition> extractInputFields(String factType) {
        List<FieldDefinition> fields = new ArrayList<>();
        
        // Determine entity class based on fact type
        Class<?> entityClass;
        String entityPrefix;
        if ("CargoReport".equalsIgnoreCase(factType)) {
            entityClass = CargoReport.class;
            entityPrefix = "cargoReport";
        } else {
            // Default to Declaration
            entityClass = Declaration.class;
            entityPrefix = "declaration";
        }
        
        // Get all declared fields from entity class
        Field[] declaredFields = entityClass.getDeclaredFields();
        
        for (Field field : declaredFields) {
            // Skip technical fields (id, createdAt, updatedAt, etc.)
            String fieldName = field.getName();
            if (shouldSkipField(fieldName)) {
                continue;
            }
            
            // Check for @Column annotation (regular fields)
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
            // Determine field type
            String type = determineFieldType(field.getType());
                
                // Build field path with entity prefix: declaration.transportMeansId or cargoReport.transportMeansId
                String fieldPath = entityPrefix + "." + fieldName;
            
            // Generate human-readable label from field name
            String label = generateLabel(fieldName);
            
            // Generate description
                String description = generateDescription(fieldPath, type);
                
                fields.add(new FieldDefinition(fieldPath, label, type, description));
                continue;
            }
            
            // Check for @OneToMany annotation (relationship fields like governmentAgencyGoodsItems)
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            if (oneToMany != null) {
                // Extract fields from the related entity (e.g., GovernmentAgencyGoodsItem)
                // Get the generic type from List<GovernmentAgencyGoodsItem>
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) genericType;
                    Type[] actualTypes = paramType.getActualTypeArguments();
                    if (actualTypes.length > 0 && actualTypes[0] instanceof Class) {
                        Class<?> relatedEntityClass = (Class<?>) actualTypes[0];
                        
                        // Extract fields from the related entity
                        Field[] relatedFields = relatedEntityClass.getDeclaredFields();
                        for (Field relatedField : relatedFields) {
                            String relatedFieldName = relatedField.getName();
                            
                            // Skip technical fields and the back-reference to parent entity
                            if (shouldSkipField(relatedFieldName) 
                                || relatedFieldName.equals("declaration")
                                || relatedFieldName.equals("cargoReport")
                                || relatedFieldName.equals("consignment")
                                || relatedField.getAnnotation(ManyToOne.class) != null) {
                                continue;
                            }
                            
                            // Check for @Column annotation
                            Column relatedColumn = relatedField.getAnnotation(Column.class);
                            if (relatedColumn != null) {
                                // Build field path with entity prefix: declaration.governmentAgencyGoodsItems.hsId or cargoReport.consignments.ucr
                                String fieldPath = entityPrefix + "." + fieldName + "." + relatedFieldName;
                                
                                // Determine field type
                                String type = determineFieldType(relatedField.getType());
                                
                                // Generate human-readable label
                                String label = generateLabel(fieldName) + " - " + generateLabel(relatedFieldName);
                                
                                // Generate description
                                String description = String.format("Field: %s (type: %s)", fieldPath, type);
                                
                                fields.add(new FieldDefinition(fieldPath, label, type, description));
                            }
                        }
                    }
                }
            }
        }
        
        return fields;
    }
    
    /**
     * Skip technical/internal fields that shouldn't be used in conditions
     */
    private static boolean shouldSkipField(String fieldName) {
        return fieldName.equals("id") 
            || fieldName.equals("createdAt") 
            || fieldName.equals("updatedAt") 
            || fieldName.equals("createdBy") 
            || fieldName.equals("updatedBy");
            // Note: governmentAgencyGoodsItems is now included via @OneToMany handling
    }
    
    /**
     * Map Java types to simplified type names for UI
     */
    private static String determineFieldType(Class<?> type) {
        if (type.equals(String.class)) {
            return "string";
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return "integer";
        } else if (type.equals(BigDecimal.class) || type.equals(Double.class) 
                || type.equals(double.class) || type.equals(Float.class) || type.equals(float.class)) {
            return "decimal";
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return "boolean";
        } else if (type.equals(LocalDateTime.class)) {
            return "string"; // Treat datetime as string for now
        } else {
            return "string"; // default
        }
    }
    
    /**
     * Convert camelCase field name to Human Readable Label
     * e.g., totalInvoiceAmount -> Total Invoice Amount
     */
    private static String generateLabel(String fieldName) {
        // Split camelCase
        String result = fieldName.replaceAll("([A-Z])", " $1");
        // Capitalize first letter
        result = result.substring(0, 1).toUpperCase() + result.substring(1);
        return result.trim();
    }
    
    /**
     * Generate description for field
     */
    private static String generateDescription(String fieldName, String type) {
        // You can customize descriptions here or load from properties file
        return String.format("Field: %s (type: %s)", fieldName, type);
    }
    
    /**
     * Get operators grouped by field type
     * Returns a map of field type -> list of applicable operators
     */
    public static Map<String, List<OperatorDefinition>> getOperatorsByType() {
        Map<String, List<OperatorDefinition>> operators = new HashMap<>();
        
        // String operators
        operators.put("string", List.of(
            new OperatorDefinition("==", "Equal to", "Check if values are equal"),
            new OperatorDefinition("!=", "Not equal to", "Check if values are not equal"),
            new OperatorDefinition("contains", "Contains", "Check if string contains a substring"),
            new OperatorDefinition("startsWith", "Starts with", "Check if string starts with a prefix"),
            new OperatorDefinition("endsWith", "Ends with", "Check if string ends with a suffix"),
            new OperatorDefinition("matches", "Matches regex", "Check if string matches a regular expression")
        ));
        
        // Integer operators
        operators.put("integer", List.of(
            new OperatorDefinition("==", "Equal to", "Check if values are equal"),
            new OperatorDefinition("!=", "Not equal to", "Check if values are not equal"),
            new OperatorDefinition(">", "Greater than", "Check if value is greater than"),
            new OperatorDefinition(">=", "Greater than or equal to", "Check if value is greater than or equal to"),
            new OperatorDefinition("<", "Less than", "Check if value is less than"),
            new OperatorDefinition("<=", "Less than or equal to", "Check if value is less than or equal to")
        ));
        
        // Decimal operators (same as integer)
        operators.put("decimal", List.of(
            new OperatorDefinition("==", "Equal to", "Check if values are equal"),
            new OperatorDefinition("!=", "Not equal to", "Check if values are not equal"),
            new OperatorDefinition(">", "Greater than", "Check if value is greater than"),
            new OperatorDefinition(">=", "Greater than or equal to", "Check if value is greater than or equal to"),
            new OperatorDefinition("<", "Less than", "Check if value is less than"),
            new OperatorDefinition("<=", "Less than or equal to", "Check if value is less than or equal to")
        ));
        
        // Boolean operators
        operators.put("boolean", List.of(
            new OperatorDefinition("==", "Equal to", "Check if values are equal"),
            new OperatorDefinition("!=", "Not equal to", "Check if values are not equal")
        ));
        
        // Array/Collection operators (for relationship fields like governmentAgencyGoodsItems)
        operators.put("array", List.of(
            new OperatorDefinition("isEmpty", "Is empty", "Check if collection is empty"),
            new OperatorDefinition("isNotEmpty", "Is not empty", "Check if collection is not empty"),
            new OperatorDefinition("size", "Size", "Get size of collection"),
            new OperatorDefinition("contains", "Contains", "Check if collection contains an element")
        ));
        
        return operators;
    }
    
    /**
     * Automatically extract all output fields from RuleOutputHit entity using reflection.
     * These fields are used in the THEN section of rules (output/action fields).
     * @return List of field definitions with predefined order and descriptions
     */
    public static List<FieldDefinition> extractOutputFields() {
        List<FieldDefinition> fields = new ArrayList<>();
        
        // Get all declared fields from RuleOutputHit class
        Class<?> entityClass = RuleOutputHit.class;
        Field[] declaredFields = entityClass.getDeclaredFields();
        
        // Define order and descriptions for output fields
        Map<String, Integer> fieldOrder = Map.of(
            "action", 0,
            "score", 1,
            "result", 2,
            "flag", 3,
            "documentType", 4,
            "documentId", 5,
            "description", 6
        );
        
        Map<String, String> fieldDescriptions = Map.of(
            "action", "Action to take when rule matches (FLAG, APPROVE, REJECT, REVIEW, HOLD)",
            "score", "Risk score contributed by this output (0-100)",
            "result", "Result message/description to display",
            "flag", "Optional flag label/category (e.g., HIGH_RISK, SUSPICIOUS)",
            "documentType", "Optional related document type (e.g., INVOICE, LICENSE)",
            "documentId", "Optional related document identifier",
            "description", "Optional longer description for reporting"
        );
        
        for (Field field : declaredFields) {
            String fieldName = field.getName();
            
            // Determine field type
            String type = determineFieldType(field.getType());
            
            // Generate human-readable label
            String label = generateLabel(fieldName);
            
            // Get description from predefined map or generate default
            String description = fieldDescriptions.getOrDefault(fieldName, 
                String.format("Field: %s (type: %s)", fieldName, type));
            
            // Get order index from predefined map
            Integer orderIndex = fieldOrder.getOrDefault(fieldName, Integer.MAX_VALUE);
            
            fields.add(new FieldDefinition(fieldName, label, type, description, orderIndex));
        }
        
        // Sort by orderIndex to ensure consistent display order
        fields.sort((a, b) -> {
            int orderA = a.getOrderIndex() != null ? a.getOrderIndex() : Integer.MAX_VALUE;
            int orderB = b.getOrderIndex() != null ? b.getOrderIndex() : Integer.MAX_VALUE;
            return Integer.compare(orderA, orderB);
        });
        
        return fields;
    }
}

