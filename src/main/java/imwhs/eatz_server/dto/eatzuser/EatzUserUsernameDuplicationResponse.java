package imwhs.eatz_server.dto.eatzuser;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class EatzUserUsernameDuplicationResponse {

    private boolean isDuplicated;

}
