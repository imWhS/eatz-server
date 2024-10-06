package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.Comment;
import imwhs.eatz_server.dto.comment.CommentResponseDto;
import imwhs.eatz_server.exception.CommentNotFoundException;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.CommentRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentQueryService {

    private final CommentRepository commentRepository;

    private final EatzUserRepository userRepository;

    private final RecipeRepository recipeRepository;

    /**
     * 페이지 번호 및 크기 기본 값.
     * 응답 메시지에 포함시킬 시용자에 대해 페이징 처리를 하기 위해 정의합니다.
     */
    private static final int DEFAULT_CURRENT_PAGE = 0;
    private static final int DEFAULT_PAGING_SIZE = 10;

    /**
     * ID로 특정 댓글 조회.
     */
    public CommentResponseDto findComment(Long id) {
        Comment comment = commentRepository.findJoinUserRecipeById(id)
                .orElseThrow(() -> new CommentNotFoundException("id " + id + "에 해당하는 댓글가 존재하지 않습니다."));
        return new CommentResponseDto(comment);
    }

    /**
     * 사용자 ID, 레시피 ID로 모든 댓글 조회.
     */
    public Page<CommentResponseDto> findComments(Long userId, Long recipeId, Integer currentPage, Integer pagingSize) {
        validateUser(userId);
        validateRecipe(recipeId);

        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findJoinUserRecipeByUserIdAndRecipeId(userId, recipeId, pageRequest);

        return comments.map(CommentResponseDto::new);
    }

    /**
     * 특정 레시피에 달린 모든 댓글 조회.
     */
    public Page<CommentResponseDto> findCommentsByRecipe(Long recipeId, Integer currentPage, Integer pagingSize) {
        validateRecipe(recipeId);

        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findJoinUserRecipeByRecipeId(recipeId, pageRequest);

        return comments.map(CommentResponseDto::new);
    }

    /**
     * 특정 사용자가 등록한 모든 댓글 조회.
     */
    public Page<CommentResponseDto> findCommentsByUser(Long userId, Integer currentPage, Integer pagingSize) {
        validateUser(userId);

        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findJoinUserRecipeByUserId(userId, pageRequest);

        return comments.map(CommentResponseDto::new);
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EatzUserNotFoundException("id가 " + userId + "인 사용자가 존재하지 않습니다.");
        }
    }

    private void validateRecipe(Long recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new RecipeNotFoundException("id가 " + recipeId + "인 레시피가 존재하지 않습니다.");
        }
    }

}
