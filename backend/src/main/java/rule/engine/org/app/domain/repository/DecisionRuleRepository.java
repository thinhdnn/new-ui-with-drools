package rule.engine.org.app.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rule.engine.org.app.domain.entity.ui.DecisionRule;
import rule.engine.org.app.domain.entity.ui.FactType;

import java.util.List;
import java.util.Optional;

public interface DecisionRuleRepository extends JpaRepository<DecisionRule, Long> {
    
    List<DecisionRule> findByActiveTrue();
    
    List<DecisionRule> findByActiveTrueOrderByPriorityAsc();
    
    // ========== VERSION MANAGEMENT QUERIES ==========
    
    /**
     * Find all versions of a rule by parent rule ID, ordered by version descending
     */
    List<DecisionRule> findByParentRuleIdOrderByVersionDesc(Long parentRuleId);
    
    /**
     * Find the latest version of a rule by parent rule ID
     */
    Optional<DecisionRule> findByParentRuleIdAndIsLatestTrue(Long parentRuleId);
    
    /**
     * Find all rules that are the latest version (for list view)
     */
    List<DecisionRule> findByIsLatestTrue();
    
    /**
     * Find all latest active rules, ordered by priority
     */
    List<DecisionRule> findByIsLatestTrueAndActiveTrueOrderByPriorityAsc();
    
    /**
     * Find all latest active rules for a specific fact type, ordered by priority
     */
    List<DecisionRule> findByFactTypeAndIsLatestTrueAndActiveTrueOrderByPriorityAsc(FactType factType);
    
    /**
     * Find rule by rule name and isLatest flag (for duplicate checking)
     */
    Optional<DecisionRule> findByRuleNameAndIsLatestTrue(String ruleName);
    
    /**
     * Find all distinct fact types
     */
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT d.factType FROM DecisionRule d WHERE d.isLatest = true")
    List<FactType> findDistinctFactTypes();
}

