package imwhs.eatz_server.dto.comment;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * CommentUserDto 클래스입니다.<br/>
 * 댓글과 연관 관계인 사용자의 기본(최소) 정보를 포함하는 DTO입니다.
 */
@Data
@AllArgsConstructor
public class EatzUserBasicDto {

    private Long id;

    private String username;

    private String imageUrl;

    public EatzUserBasicDto(EatzUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

}
