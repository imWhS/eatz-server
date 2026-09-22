package imwhs.eatz_server.dto.affiliate;

import imwhs.eatz_server.domain.RequirementType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

@Getter
public class AffiliateCreateRequest {

    @NotNull
    private RequirementType requirementType;

    @NotNull
    private Long requirementId;

    @URL
    private String url;

}
