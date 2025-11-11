package rule.engine.org.app.domain.entity.ui;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rule.engine.org.app.domain.entity.common.BaseAuditableEntity;

@Entity
@Table(name = "rule_output_group")
@Data
@EqualsAndHashCode(callSuper = true)
public class RuleOutputGroup extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_rule_id", nullable = false)
    private DecisionRule decisionRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private RuleOutputGroup parent;

    @Convert(converter = RuleGroupTypeConverter.class)
    @Column(name = "type", nullable = false, columnDefinition = "group_type")
    @org.hibernate.annotations.ColumnTransformer(write = "?::group_type")
    private RuleGroupType type;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;
}

