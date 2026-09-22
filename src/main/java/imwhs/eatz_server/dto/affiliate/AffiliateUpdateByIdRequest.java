package imwhs.eatz_server.dto.affiliate;

import lombok.Getter;
import org.hibernate.validator.constraints.URL;

@Getter
public class AffiliateUpdateByIdRequest {

    @URL
    private String url;

}
