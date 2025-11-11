package rule.engine.org.app.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rule.engine.org.app.domain.entity.ui.DecisionRule;
import rule.engine.org.app.domain.entity.ui.RuleConditionGroup;

import java.util.List;

public interface RuleConditionGroupRepository extends JpaRepository<RuleConditionGroup, Long> {
    List<RuleConditionGroup> findByDecisionRuleOrderByOrderIndexAsc(DecisionRule decisionRule);
    
    List<RuleConditionGroup> findByDecisionRuleIdOrderByOrderIndexAsc(Long decisionRuleId);
}


