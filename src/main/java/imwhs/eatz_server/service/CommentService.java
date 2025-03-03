package imwhs.eatz_server.service;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.domain.recipe.Comment;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.comment.CommentItemDto;
import imwhs.eatz_server.dto.comment.CommentWithRecipeResponseDto;
import imwhs.eatz_server.exception.*;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    /**
     * 새 댓글을 등록합니다.
     *
     * @param recipeId 댓글을 달 레시피의 ID
     * @param content  등록할 댓글의 내용
     * @return 등록 완료된 댓글의 ID.
     */
    @Transactional
    public Long register(Long recipeId, String content) {
        String username = EatzUserAuthUtil.getUsername();

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));
        EatzUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EatzUserNotFoundException(username));

        Comment comment = new Comment(user, recipe, content);
        commentRepository.save(comment);

        return comment.getId();
    }

    /**
     * 댓글을 업데이트합니다.
     * @param commentId 업데이트할 댓글의 ID
     * @param userId 댓글 업데이트를 요청한 사용자의 ID
     * @param content 업데이트할 댓글의 내용
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
     * 레시피에 추가된 댓글 목록을 조회합니다.
     * @param id 조회할 댓글의 식별자
     * @return
     */
    public Page<CommentItemDto> findAllByRecipe(Long id, Pageable pageable) {
        validateRecipe(id);
        Page<CommentItemDto> dto = commentRepository.findWithUserByRecipeId(id, pageable);
        return dto;
    }

    /**
     * 특정 사용자가 등록한 모든 댓글을 조회합니다.
     */
    public Page<CommentWithRecipeResponseDto> findCommentsByUser(String username, Pageable pageable) {
        validateUser(username);
        Page<CommentWithRecipeResponseDto> dto = commentRepository.findWithRecipeByUserUsername(username, pageable);
        return dto;
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

    private void validateUser(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new EatzUserNotFoundException(username);
        }
    }

    private void validateRecipe(Long recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new RecipeNotFoundException(recipeId);
        }
    }

}
