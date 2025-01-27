package imwhs.eatz_server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Role;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * LikedUserDto 클래스입니다.<br/>
 * 좋아요한 사용자 정보를 전달하기 위해 사용합니다.
 */
@Data
public class LikedUserDto {

    private Long id;

    private String username;

    private String email;

    private String imageUrl;

    public LikedUserDto(EatzUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.imageUrl = user.getImageUrl();
    }

}
