package rule.engine.org.app.domain.entity.ui;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RuleGroupTypeConverter implements AttributeConverter<RuleGroupType, String> {
    
    @Override
    public String convertToDatabaseColumn(RuleGroupType attribute) {
        if (attribute == null) {
            return null;
        }
        // Return enum name - PostgreSQL will cast it to enum type
        return attribute.name();
    }
    
    @Override
    public RuleGroupType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return RuleGroupType.valueOf(dbData);
    }
}

