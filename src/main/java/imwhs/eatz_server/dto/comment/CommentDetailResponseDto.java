package imwhs.eatz_server.dto.comment;

import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.recipe.RecipeSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CommentDetailResponseDto 클래스입니다.
 * <ul>
 *     <li>댓글의 기본 정보 뿐 아니라 댓글을 등록한 사용자, 댓글이 달려 있는 레시피의 부가 정보를 포함하는 DTO입니다.</li>
 *     <li>특정 단일 댓글의 상세 정보를 조회해야 하는 경우에 주로 사용합니다.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class CommentDetailResponseDto {

    private Long id;

    private EatzUserSummaryDto user;

    private RecipeSummaryDto recipe;

    private String content;

    // TODO: 작성일, 수정일, 삭제일 추가

}
