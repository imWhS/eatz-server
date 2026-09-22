package imwhs.eatz_server.dto.affiliate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

@Getter
public class AffiliateUpdateByIdRequest {

    @NotBlank
    @URL
    private String url;

}
