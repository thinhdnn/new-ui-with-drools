package rule.engine.org.app.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rule.engine.org.app.domain.entity.ui.KieContainerVersion;
import rule.engine.org.app.domain.entity.ui.FactType;

import java.util.List;
import java.util.Optional;

public interface KieContainerVersionRepository extends JpaRepository<KieContainerVersion, Long> {
    
    /**
     * Find by version number
     */
    Optional<KieContainerVersion> findByVersion(Long version);
    
    /**
     * Find by fact type and version
     */
    Optional<KieContainerVersion> findByFactTypeAndVersion(FactType factType, Long version);
    
    /**
     * Find latest version (top 1 ordered by version descending)
     */
    Optional<KieContainerVersion> findTopByOrderByVersionDesc();
    
    /**
     * Find latest version for a specific fact type
     */
    Optional<KieContainerVersion> findTopByFactTypeOrderByVersionDesc(FactType factType);
    
    /**
     * Alias for findTopByOrderByVersionDesc (deprecated - use findLatestVersionByFactType instead)
     */
    @Deprecated
    default Optional<KieContainerVersion> findLatestVersion() {
        return findTopByOrderByVersionDesc();
    }
    
    /**
     * Find latest version for a specific fact type
     */
    default Optional<KieContainerVersion> findLatestVersionByFactType(String factType) {
        FactType factTypeEnum = FactType.fromValue(factType);
        return findTopByFactTypeOrderByVersionDesc(factTypeEnum);
    }
    
    /**
     * Find all versions ordered by version descending (newest first)
     */
    List<KieContainerVersion> findAllByOrderByVersionDesc();
    
    /**
     * Find all versions for a specific fact type, ordered by version descending
     */
    List<KieContainerVersion> findAllByFactTypeOrderByVersionDesc(FactType factType);
    
    /**
     * Find all versions ordered by created date descending (newest first)
     */
    List<KieContainerVersion> findAllByOrderByCreatedAtDesc();
    
    /**
     * Find all distinct fact types
     */
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT k.factType FROM KieContainerVersion k")
    List<FactType> findDistinctFactTypes();
}

