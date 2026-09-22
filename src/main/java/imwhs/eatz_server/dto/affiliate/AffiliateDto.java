package imwhs.eatz_server.dto.affiliate;

import imwhs.eatz_server.domain.Affiliate;
import imwhs.eatz_server.domain.RequirementType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AffiliateDto {

    private Long id;

    private RequirementType requirementType;

    private Long requirementId;

    public static AffiliateDto from(Affiliate affiliate) {
        return new AffiliateDto(affiliate.getId(), affiliate.getRequirementType(), affiliate.getRequirementId());
    }

}
