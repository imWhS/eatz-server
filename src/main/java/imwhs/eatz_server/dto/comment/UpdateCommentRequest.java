package imwhs.eatz_server.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 댓글 업데이트를 요청하기 위해 필요한 정보를 담은 DTO입니다.
 */
@Data
public class UpdateCommentRequest {

    @NotBlank
    private String content;

}
