package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.AffiliateLink;
import imwhs.eatz_server.domain.RequirementType;
import imwhs.eatz_server.exception.AffiliateLinkNotFoundException;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import imwhs.eatz_server.repository.AffiliateLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AffiliateLinkService {

    private final AffiliateLinkRepository affiliateLinkRepository;

    public AffiliateLink get(RequirementType requirementType, Long requirementId) {
        return affiliateLinkRepository.findByRequirementTypeAndRequirementId(requirementType, requirementId)
                .orElseThrow(() -> new AffiliateLinkNotFoundException());
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(RequirementType requirementType, Long requirementId, String url) {
        if (affiliateLinkRepository.existsByRequirementTypeAndRequirementId(requirementType, requirementId)) {
            throw new EatzInvalidRequestArgumentException("이미 제휴 주소 정보가 존재하는 준비물이에요.");
        }

        AffiliateLink affiliateLink = AffiliateLink.create(requirementType, requirementId, url);
        affiliateLinkRepository.save(affiliateLink);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(RequirementType requirementType, Long requirementId, String url) {
        AffiliateLink affiliateLink = affiliateLinkRepository.findByRequirementTypeAndRequirementId(requirementType, requirementId)
                .orElseThrow(() -> new AffiliateLinkNotFoundException());
        affiliateLink.updateUrl(url);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateById(Long id, String url) {
        AffiliateLink affiliateLink = affiliateLinkRepository.findById(id).orElseThrow(() -> new AffiliateLinkNotFoundException());
        affiliateLink.updateUrl(url);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(RequirementType requirementType, Long requirementId) {
        affiliateLinkRepository.deleteByRequirementTypeAndRequirementId(requirementType, requirementId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        affiliateLinkRepository.deleteById(id);
    }

}
