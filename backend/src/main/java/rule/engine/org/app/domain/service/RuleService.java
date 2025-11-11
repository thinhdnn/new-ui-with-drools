package rule.engine.org.app.domain.service;

import org.springframework.stereotype.Service;
import rule.engine.org.app.domain.entity.execution.declaration.Declaration;
import rule.engine.org.app.domain.entity.execution.TotalRuleResults;

@Service
public class RuleService {
    
    private final RuleEngineManager ruleEngineManager;

    public RuleService(RuleEngineManager ruleEngineManager) {
        this.ruleEngineManager = ruleEngineManager;
    }

    public TotalRuleResults fireRules(Declaration declaration) {
        return ruleEngineManager.fireRules(declaration);
    }
}


