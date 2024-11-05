package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Comment;
import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.comment.CommentResponseDto;
import imwhs.eatz_server.exception.*;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    /**
     * 페이지 번호 및 크기 기본 값.
     * <p>
     *     응답 메시지에 포함시킬 시용자에 대해 페이징 처리를 하기 위해 정의합니다.
     * </p>
     */
    private static final int DEFAULT_CURRENT_PAGE = 0;
    private static final int DEFAULT_PAGING_SIZE = 10;

    private final CommentRepository commentRepository;

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    /**
     * 새 댓글을 등록합니다.
     * @param recipeId 댓글을 달 레시피의 ID
     * @param userId 댓글 등록을 요청한 사용자의 ID
     * @param content 등록할 댓글의 내용
     * @return 등록 완료된 댓글의 ID
     */
    @Transactional
    public Long registerComment(Long recipeId, Long userId, String content) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + recipeId + "인 레시피가 존재하지 않습니다."));
        EatzUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EatzUserNotFoundException("id가 " + userId + "인 사용자가 존재하지 않습니다."));

        Comment comment = new Comment(user, recipe, content);
        commentRepository.save(comment);

        return comment.getId();
    }

    /**
     * 댓글을 수정합니다.
     * @param commentId 수정할 댓글의 ID
     * @param userId 댓글 수정을 요청한 사용자의 ID
     * @param content 수정할 댓글의 내용
     */
    @Transactional
    public void updateComment(Long commentId, Long userId, String content) {
        Comment comment = getComment(commentId, userId);
        comment.updateContent(content);
    }

    /**
     * 댓글을 삭제 처리합니다.
     * @param commentId 삭제 처리할 댓글의 ID
     * @param userId 댓글 삭제 처리를 요청한 사용자의 ID
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getComment(commentId, userId);
        comment.markAsDeleted();
    }

    /**
     * 식별자로 댓글을 조회합니다.
     * @param id 조회할 댓글의 식별자
     * @return
     */
    public CommentResponseDto findComment(Long id) {
        Comment comment = commentRepository.findWithUserRecipeById(id)
                .orElseThrow(() -> new CommentNotFoundException("id " + id + "에 해당하는 댓글이 존재하지 않습니다."));
        return new CommentResponseDto(comment);
    }

    /**
     * 사용자와 레시피 각 식별자에 해당하는 모든 댓글을 조회합니다.
     * @
     */
    public Page<CommentResponseDto> findComments(Long userId, Long recipeId, Integer currentPage, Integer pagingSize) {
        validateUser(userId);
        validateRecipe(recipeId);

        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findWithUserRecipeByUserIdAndRecipeId(userId, recipeId, pageRequest);

        return comments.map(CommentResponseDto::new);
    }

    /**
     * 식별자로 Comment 엔티티를 가져옵니다.
     * @param id 댓글 식별자
     * @param userId 엔티티를 요청한 사용자 ID
     * @throws CommentNotFoundException commentId에 해당하는 Comment 엔티티가 존재하지 않을 경우
     * @throws UnauthorizedEatzUserException 댓글을 등록한 사용자의 id가 userId와 일치하지 않을 경우(접근 권한이 없는 사용자의 요청인 경우)
     * @throws IllegalArgumentException 가져오려는 댓글이 삭제 처리된 경우
     * @return Comment 엔티티
     */
    private Comment getComment(Long id, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("id가 " + id + "인 댓글이 존재하지 않습니다."));

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new UnauthorizedEatzUserException("댓글을 등록한 사용자가 아니어서, 권한이 없습니다.");
        }

        if (comment.isMarkedAsDeleted()) {
            throw new IllegalArgumentException("삭제 처리된 댓글입니다.");
        }

        return comment;
    }

    /**
     * 특정 레시피에 달린 모든 댓글을 조회합니다.
     */
    public Page<CommentResponseDto> findCommentsByRecipe(Long recipeId, Integer currentPage, Integer pagingSize) {
        validateRecipe(recipeId);

        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findWithUserRecipeByRecipeId(recipeId, pageRequest);

        return comments.map(CommentResponseDto::new);
    }

    /**
     * 특정 사용자가 등록한 모든 댓글을 조회합니다.
     */
    public Page<CommentResponseDto> findCommentsByUser(Long userId, Integer currentPage, Integer pagingSize) {
        validateUser(userId);

        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findWithUserRecipeByUserId(userId, pageRequest);

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
