package rule.engine.org.app.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rule.engine.org.app.domain.entity.ui.DecisionRule;
import rule.engine.org.app.domain.entity.ui.RuleOutput;

import java.util.List;

public interface RuleOutputRepository extends JpaRepository<RuleOutput, Long> {
    List<RuleOutput> findByDecisionRuleOrderByOrderIndexAsc(DecisionRule decisionRule);
    
    List<RuleOutput> findByDecisionRuleIdOrderByOrderIndexAsc(Long decisionRuleId);
}

