package rule.engine.org.app.domain.entity.ui;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RuleOperatorTypeConverter implements AttributeConverter<RuleOperatorType, String> {
    
    @Override
    public String convertToDatabaseColumn(RuleOperatorType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }
    
    @Override
    public RuleOperatorType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return RuleOperatorType.valueOf(dbData);
    }
}

