package imwhs.eatz_server.dto.eatzuser;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * EatzUserMinimumDto 클래스입니다.<br/>
 * 사용자에 대한 간략한 정보를 전달하기 위해 사용합니다.
 */
@AllArgsConstructor
@Data
public class EatzUserBasicDto {

    private Long id;

    private String username;

    private String imageUrl;

    public EatzUserBasicDto(EatzUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.imageUrl = user.getImageUrl();
    }

}
