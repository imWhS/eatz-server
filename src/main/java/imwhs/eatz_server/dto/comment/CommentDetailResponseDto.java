package imwhs.eatz_server.dto.comment;

import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.recipe.RecipeSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CommentDetailResponseDto 클래스입니다.
 * <p>
 *     특정 댓글의 상세 정보 데이터를 전달하기 위해 사용하는 DTO 클래스입니다.
 *     댓글 뿐 아니라 댓글을 작성한 사용자, 댓글이 달린 레시피에 대한 데이터를 포함합니다.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDetailResponseDto {

    private Long id;

    private EatzUserSummaryDto user;

    private RecipeSummaryDto recipe;

    private String content;

}
