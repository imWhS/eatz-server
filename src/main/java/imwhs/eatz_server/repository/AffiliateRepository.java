package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.Affiliate;
import imwhs.eatz_server.domain.RequirementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AffiliateRepository extends JpaRepository<Affiliate, Long> {

    Optional<Affiliate> findByRequirementTypeAndRequirementId(RequirementType requirementType, Long requirementId);

    void deleteByRequirementTypeAndRequirementId(RequirementType requirementType, Long requirementId);

    boolean existsByRequirementTypeAndRequirementId(RequirementType requirementType, Long requirementId);

}
