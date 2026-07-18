package imwhs.eatz_server.dto.eatzuser;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 사용자에 대한 핵심 정보를 전달할 때 사용합니다.
 */
@AllArgsConstructor
@Data
public class EatzUserEssentialDto {

    /**
     * 사용자의 ID
     */
    private Long id;

    /**
     * 사용자의 사용자 이름
     */
    private String username;

    /**
     * 사용자의 대표 이미지 URL
     */
    private String imageUrl;

    public EatzUserEssentialDto(EatzUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.imageUrl = user.getImageUrl();
    }

}
