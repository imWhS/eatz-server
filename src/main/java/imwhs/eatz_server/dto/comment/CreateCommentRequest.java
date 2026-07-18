package imwhs.eatz_server.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 레시피 생성을 요청하기 위해 필요한 정보를 담은 DTO입니다.
 */
@Data
public class CreateCommentRequest {

    @NotBlank
    private String content;

}
