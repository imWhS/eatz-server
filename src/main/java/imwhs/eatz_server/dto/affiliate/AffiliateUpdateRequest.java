package imwhs.eatz_server.dto.affiliate;

import imwhs.eatz_server.domain.RequirementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

@Getter
public class AffiliateUpdateRequest {

    @NotNull
    private RequirementType requirementType;

    @NotNull
    private Long requirementId;

    @NotBlank
    @URL
    private String url;

    @NotBlank
    private String provider;

}
