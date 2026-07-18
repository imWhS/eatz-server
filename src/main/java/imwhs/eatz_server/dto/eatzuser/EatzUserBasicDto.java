package imwhs.eatz_server.dto.eatzuser;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.eatzuser.EatzUserRole;
import lombok.Data;

/**
 * 현재 로그인 된 사용자 정보(/me)를 전달할 때 사용합니다.
 */
@Data
public class EatzUserBasicDto {

    /**
     * 사용자의 ID
     */
    private Long id;

    /**
     * 사용자의 사용자 이름
     */
    private String username;

    /**
     * 사용자의 이메일 주소
     */
    private String email;

    /**
     * 사용자의 대표 이미지 URL
     */
    private String imageUrl;

    /**
     * 사용자의 역할
     */
    private EatzUserRole role;

    public EatzUserBasicDto(EatzUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.imageUrl = user.getImageUrl();
        this.role = user.getEatzUserRole();
    }

}
