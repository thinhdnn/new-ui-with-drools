package rule.engine.org.app.domain.entity.ui;

/**
 * Operator type supported by the rule engine for building conditions.
 */
public enum RuleOperatorType {
    EQUALS,
    NOT_EQUALS,
    GT,
    GTE,
    LT,
    LTE,
    IN,
    NOT_IN,
    BETWEEN,
    STR_CONTAINS,
    STR_STARTS_WITH,
    STR_ENDS_WITH,
    MATCHES,
    IS_NULL,
    IS_NOT_NULL
}


