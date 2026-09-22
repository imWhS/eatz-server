package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Affiliate;
import imwhs.eatz_server.domain.RequirementType;
import imwhs.eatz_server.dto.affiliate.AffiliateDto;
import imwhs.eatz_server.exception.AffiliateNotFoundException;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import imwhs.eatz_server.repository.AffiliateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AffiliateService {

    private final AffiliateRepository affiliateRepository;

    @Value("${affiliate.default-url}")
    private String affiliateDefaultUrl;

    @Value("${affiliate.default-provider}")
    private String affiliateDefaultProvider;

    public AffiliateDto get(RequirementType requirementType, Long requirementId) {
        if (requirementType == null && requirementId == null) {
            return new AffiliateDto(
                    null,
                    null,
                    affiliateDefaultUrl,
                    affiliateDefaultProvider
            );
        }

        if (requirementType == null || requirementId == null) {
            throw new EatzInvalidRequestArgumentException(
                    "준비물 타입(재료/도구 여부)과 ID가 함께 전달돼야 해요."
            );
        }

         return affiliateRepository.findByRequirementTypeAndRequirementId(requirementType, requirementId)
                 .map(affiliate ->
                         new AffiliateDto(
                             affiliate.getRequirementType(),
                             affiliate.getRequirementId(),
                             affiliate.getUrl(),
                             affiliate.getProvider()))
                 .orElseGet(() ->
                         new AffiliateDto(
                                 requirementType,
                                 requirementId,
                                 affiliateDefaultUrl,
                                 affiliateDefaultProvider));
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(RequirementType requirementType, Long requirementId, String url, String provider) {
        if (affiliateRepository.existsByRequirementTypeAndRequirementId(requirementType, requirementId)) {
            throw new EatzInvalidRequestArgumentException("이미 제휴 정보가 존재하는 준비물이에요.");
        }

        Affiliate affiliate = Affiliate.create(requirementType, requirementId, url, provider);
        affiliateRepository.save(affiliate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUrl(RequirementType requirementType, Long requirementId, String url) {
        Affiliate affiliate = affiliateRepository.findByRequirementTypeAndRequirementId(requirementType, requirementId)
                .orElseThrow(() -> new AffiliateNotFoundException());
        affiliate.updateUrl(url);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateProvider(RequirementType requirementType, Long requirementId, String provider) {
        Affiliate affiliate = affiliateRepository.findByRequirementTypeAndRequirementId(requirementType, requirementId)
                .orElseThrow(() -> new AffiliateNotFoundException());
        affiliate.updateProvider(provider);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUrlById(Long id, String url) {
        Affiliate affiliate = affiliateRepository.findById(id).orElseThrow(() -> new AffiliateNotFoundException());
        affiliate.updateUrl(url);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(
            RequirementType requirementType,
            Long requirementId,
            String url,
            String provider
    ) {
        Affiliate affiliate = affiliateRepository.findByRequirementTypeAndRequirementId(requirementType, requirementId)
                .orElseThrow(() -> new AffiliateNotFoundException());
        affiliate.updateUrl(url);
        affiliate.updateProvider(provider);
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
