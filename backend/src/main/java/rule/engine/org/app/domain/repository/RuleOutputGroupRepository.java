package rule.engine.org.app.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rule.engine.org.app.domain.entity.ui.DecisionRule;
import rule.engine.org.app.domain.entity.ui.RuleOutputGroup;

import java.util.List;

public interface RuleOutputGroupRepository extends JpaRepository<RuleOutputGroup, Long> {
    List<RuleOutputGroup> findByDecisionRuleOrderByOrderIndexAsc(DecisionRule decisionRule);
    
    List<RuleOutputGroup> findByDecisionRuleIdOrderByOrderIndexAsc(Long decisionRuleId);
}

