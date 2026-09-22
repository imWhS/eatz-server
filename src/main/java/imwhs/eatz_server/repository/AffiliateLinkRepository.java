package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.AffiliateLink;
import imwhs.eatz_server.domain.RequirementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AffiliateLinkRepository extends JpaRepository<AffiliateLink, Long> {

    Optional<AffiliateLink> findByRequirementTypeAndRequirementId(RequirementType requirementType, Long requirementId);

    void deleteByRequirementTypeAndRequirementId(RequirementType requirementType, Long requirementId);

    boolean existsByRequirementTypeAndRequirementId(RequirementType requirementType, Long requirementId);

}
