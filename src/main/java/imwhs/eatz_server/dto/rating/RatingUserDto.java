package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * RatingUserDto 클래스입니다.<br/>
 * 평가와 연관 관계인 사용자의 간략한 정보를 전달하기 위해 사용합니다.
 */
@Data
@AllArgsConstructor
public class RatingUserDto {

    private Long id;

    private String username;

    public RatingUserDto(EatzUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

}
