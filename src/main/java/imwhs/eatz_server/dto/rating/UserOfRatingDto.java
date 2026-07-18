package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 평가와 연관 관계인 사용자의 간략한 정보를 전달할 때 사용합니다.
 */
@Data
@AllArgsConstructor
public class UserOfRatingDto {

    private Long id;

    private String username;

    public UserOfRatingDto(EatzUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

}
