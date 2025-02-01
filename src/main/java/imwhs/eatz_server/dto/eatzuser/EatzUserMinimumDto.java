package imwhs.eatz_server.dto.eatzuser;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import lombok.Data;

/**
 * EatzUserMinimumDto 클래스입니다.<br/>
 * 사용자에 대한 간략한 정보를 전달하기 위해 사용합니다.
 */
@Data
public class EatzUserMinimumDto {

    private Long id;

    private String username;

    private String email;

    private String imageUrl;

    public EatzUserMinimumDto(EatzUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.imageUrl = user.getImageUrl();
    }

}
