package imwhs.eatz_server.repository.comment;

import imwhs.eatz_server.dto.comment.CommentBasicDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentQueryRepository {

    /**
     * 레시피에 달린 모든 댓글의 기본 정보 목록을 조회합니다.
     * <ul>
     *     <li> 최근에 등록된 댓글부터 정렬합니다. </li>
     *     <li> 회원 탈퇴한 사용자의 댓글도 목록에 포함합니다. </li>
     *     <li> 숨김 처리된 댓글은 조회 대상에서 제외합니다. </li>
     *     <li> 사용자의 ID를 전달받으면, 해당 사용자가 차단한 사용자의 댓글은 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @param userId 사용자의 ID
     * @return 조회된 댓글의 기본 정보 목록 및 페이징 정보
     */
    Page<CommentBasicDto> findAllBasicsByRecipeId(Long id, Long userId, Pageable pageable);

}
