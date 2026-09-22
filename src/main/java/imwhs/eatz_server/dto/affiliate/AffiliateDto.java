package imwhs.eatz_server.dto.affiliate;

import imwhs.eatz_server.domain.RequirementType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AffiliateDto {

    private RequirementType requirementType;

    private Long requirementId;

    private String url;

    private String provider;

}
