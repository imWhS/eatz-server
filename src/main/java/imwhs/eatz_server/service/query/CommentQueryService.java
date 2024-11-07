package imwhs.eatz_server.service.query;

import imwhs.eatz_server.dto.PagedResponse;
import imwhs.eatz_server.dto.comment.CommentByUserResponseDto;
import imwhs.eatz_server.dto.comment.CommentDetailResponseDto;
import imwhs.eatz_server.dto.comment.CommentByRecipeResponseDto;
import imwhs.eatz_server.exception.CommentNotFoundException;
import imwhs.eatz_server.repository.comment.CommentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * 식별자로 댓글과 관련된 상세 정보를 조회합니다.<br/>
     * 식별자에 해당하는 댓글의 기본 정보와 댓글을 등록한 사용자, 댓글이 달린 레시피의 부가 정보를 조회합니다.
     */
    public CommentDetailResponseDto findCommentDetail(Long id) {
        return commentQueryRepository.findCommentDetailById(id)
                .orElseThrow(() -> new CommentNotFoundException("id " + id + "에 해당하는 댓글이 존재하지 않습니다."));
    }

    /**
     * 특정 레시피에 달린 모든 댓글과 작성자 정보를 조회합니다.<br/>
     * 레시피에 달린 모든 댓글 별 기본 정보와 해당 댓글을 등록한 사용자의 부가 정보를 조회합니다.
     * @param id 레시피 식별자
     */
    public PagedResponse<CommentByRecipeResponseDto> findCommentsByRecipe(Long id, Integer currentPage, Integer pagingSize) {
        int page = currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage;
        int size = pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize;

        List<CommentByRecipeResponseDto> data = commentQueryRepository.findCommentsByRecipe(id, page, size);
        Long totalItems = commentQueryRepository.countCommentsByRecipe(id);
        return PagedResponse.of(data, totalItems, (int) Math.ceil((double) totalItems / size), page, size);
    }

    /**
     * 특정 사용자가 등록한 모든 댓글과 레시피 정보를 조회합니다.<br/>
     * 사용자가 등록한 모든 댓글 별 기본 정보와 해당 댓글이 달린 레시피의 부가 정보를 조회합니다.
     * @param id 레시피 식별자
     */
    public PagedResponse<CommentByUserResponseDto> findCommentsByUser(Long id, Integer currentPage, Integer pagingSize) {
        int page = currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage;
        int size = pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize;

        List<CommentByUserResponseDto> data = commentQueryRepository.findCommentsByUser(id, page, size);
        Long totalItems = commentQueryRepository.countCommentsByUser(id);
        return PagedResponse.of(data, totalItems, (int) Math.ceil((double) totalItems / size), page, size);
    }

}
