package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.Comment;
import imwhs.eatz_server.dto.comment.CommentDetailResponseDto;
import imwhs.eatz_server.dto.comment.CommentResponseDto;
import imwhs.eatz_server.exception.CommentNotFoundException;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.comment.CommentQueryRepository;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentQueryService {

    /**
     * 페이지 번호 및 크기 기본 값.
     * <p>
     *     응답 메시지에 포함시킬 시용자에 대해 페이징 처리를 하기 위해 정의합니다.
     * </p>
     */
    private static final int DEFAULT_CURRENT_PAGE = 0;
    private static final int DEFAULT_PAGING_SIZE = 10;

    private final CommentQueryRepository commentQueryRepository;

    /**
     * 식별자에 해당하는 댓글의 상세 정보를 조회합니다.
     * <ul>
     *     <li>식별자로 댓글과 댓글을 작성한 사용자, 댓글이 달려 있는 레시피 정보를 함께 조회합니다.</li>
     * </ul>
     */
    public CommentDetailResponseDto findCommentDetail(Long id) {
        return commentQueryRepository.findCommentDetailById(id)
                .orElseThrow(() -> new CommentNotFoundException("id " + id + "에 해당하는 댓글이 존재하지 않습니다."));
    }

}
