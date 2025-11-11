package rule.engine.org.app.domain.entity.ui;

/**
 * Value type to drive parsing/coercion and operator validation.
 */
public enum RuleValueType {
    STRING,
    INT,
    LONG,
    BIG_DECIMAL,
    DATE,
    BOOLEAN,
    ENUM,
    ARRAY,
    JSON
}


