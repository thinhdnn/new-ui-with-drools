package rule.engine.org.app.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rule.engine.org.app.domain.entity.ui.RuleCondition;
import rule.engine.org.app.domain.entity.ui.RuleConditionGroup;

import java.util.List;

public interface RuleConditionRepository extends JpaRepository<RuleCondition, Long> {
    List<RuleCondition> findByGroupOrderByOrderIndexAsc(RuleConditionGroup group);
}


