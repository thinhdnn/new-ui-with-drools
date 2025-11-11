package rule.engine.org.app.domain.entity.ui;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RuleValueTypeConverter implements AttributeConverter<RuleValueType, String> {
    
    @Override
    public String convertToDatabaseColumn(RuleValueType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }
    
    @Override
    public RuleValueType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return RuleValueType.valueOf(dbData);
    }
}

