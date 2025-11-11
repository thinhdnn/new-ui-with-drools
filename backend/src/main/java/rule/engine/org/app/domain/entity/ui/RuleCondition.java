package rule.engine.org.app.domain.entity.ui;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rule.engine.org.app.domain.entity.common.BaseAuditableEntity;

@Entity
@Table(name = "rule_condition")
@Data
@EqualsAndHashCode(callSuper = true)
public class RuleCondition extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private RuleConditionGroup group;

    @Column(name = "field_path", length = 512, nullable = false)
    private String fieldPath;

    @Convert(converter = RuleOperatorTypeConverter.class)
    @Column(name = "operator", nullable = false, columnDefinition = "operator_type")
    @org.hibernate.annotations.ColumnTransformer(write = "?::operator_type")
    private RuleOperatorType operator;

    @Convert(converter = RuleValueTypeConverter.class)
    @Column(name = "value_type", nullable = false, columnDefinition = "value_type")
    @org.hibernate.annotations.ColumnTransformer(write = "?::value_type")
    private RuleValueType valueType;

    // Value columns (only one should be used depending on valueType)
    @Column(name = "value_text", columnDefinition = "text")
    private String valueText;

    @Column(name = "value_number")
    private Long valueNumber;

    @Column(name = "value_decimal", precision = 38, scale = 12)
    private java.math.BigDecimal valueDecimal;

    @Column(name = "value_boolean")
    private Boolean valueBoolean;

    @Column(name = "value_date")
    private java.time.LocalDateTime valueDate;

    @Column(name = "value_json", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String valueJson;

    @Column(name = "options_json", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String optionsJson;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;
}


