package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Affiliate;
import imwhs.eatz_server.domain.RequirementType;
import imwhs.eatz_server.exception.AffiliateNotFoundException;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import imwhs.eatz_server.repository.AffiliateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AffiliateService {

    private final AffiliateRepository affiliateRepository;

    public Affiliate get(RequirementType requirementType, Long requirementId) {
        return affiliateRepository.findByRequirementTypeAndRequirementId(requirementType, requirementId)
                .orElseThrow(() -> new AffiliateNotFoundException());
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(RequirementType requirementType, Long requirementId, String url) {
        if (affiliateRepository.existsByRequirementTypeAndRequirementId(requirementType, requirementId)) {
            throw new EatzInvalidRequestArgumentException("이미 제휴 정보가 존재하는 준비물이에요.");
        }

        Affiliate affiliate = Affiliate.create(requirementType, requirementId, url);
        affiliateRepository.save(affiliate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(RequirementType requirementType, Long requirementId, String url) {
        Affiliate affiliate = affiliateRepository.findByRequirementTypeAndRequirementId(requirementType, requirementId)
                .orElseThrow(() -> new AffiliateNotFoundException());
        affiliate.updateUrl(url);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateById(Long id, String url) {
        Affiliate affiliate = affiliateRepository.findById(id).orElseThrow(() -> new AffiliateNotFoundException());
        affiliate.updateUrl(url);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(RequirementType requirementType, Long requirementId) {
        affiliateRepository.deleteByRequirementTypeAndRequirementId(requirementType, requirementId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        affiliateRepository.deleteById(id);
    }

}
