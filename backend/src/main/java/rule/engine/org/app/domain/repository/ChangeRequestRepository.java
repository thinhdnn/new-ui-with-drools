package rule.engine.org.app.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rule.engine.org.app.domain.entity.ui.ChangeRequest;
import rule.engine.org.app.domain.entity.ui.ChangeRequestStatus;
import rule.engine.org.app.domain.entity.ui.FactType;

import java.util.List;

@Repository
public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, Long> {

    /**
     * Find all change requests for a specific fact type
     */
    List<ChangeRequest> findByFactTypeOrderByCreatedAtDesc(FactType factType);

    /**
     * Find all change requests by status
     */
    List<ChangeRequest> findByStatusOrderByCreatedAtDesc(ChangeRequestStatus status);

    /**
     * Find all change requests for a specific fact type and status
     */
    List<ChangeRequest> findByFactTypeAndStatusOrderByCreatedAtDesc(FactType factType, ChangeRequestStatus status);

    /**
     * Find all pending change requests
     */
    @Query("SELECT cr FROM ChangeRequest cr WHERE cr.status = 'PENDING' ORDER BY cr.createdAt DESC")
    List<ChangeRequest> findAllPendingOrderByCreatedAtDesc();

    /**
     * Find all change requests ordered by creation date descending
     */
    List<ChangeRequest> findAllByOrderByCreatedAtDesc();

    /**
     * Find distinct fact types that have change requests
     */
    @Query("SELECT DISTINCT cr.factType FROM ChangeRequest cr")
    List<FactType> findDistinctFactTypes();
}

