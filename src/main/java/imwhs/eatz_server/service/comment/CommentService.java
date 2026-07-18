package imwhs.eatz_server.service.comment;

import imwhs.eatz_server.domain.Comment;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.comment.*;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 댓글(Comment) 상태를 변경할 수 있는 서비스입니다.
 *
 * <ul>
 *     <li> Comment 생성 뿐 아니라, 수정, 삭제 등 엔티티 데이터를 변경하는 쓰기 전용 비즈니스 로직을 담당합니다. </li>
 *     <li> 읽기 전용 비즈니스 로직은 RatingQueryService에서 처리합니다. </li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final EatzUserRepository userRepository;

    /**
     * 댓글을 생성하고, 레시피 등 댓글과 연관된 정보를 매핑한 후 저장합니다.
     * @param recipeId 댓글을 달 레시피의 ID
     * @param authorId 댓글 등록을 요청한 사용자의 ID (댓글 작성자의 ID)
     * @param content 댓글의 내용
     * @return 생성된 댓글의 기본 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public CommentBasicDto register(Long recipeId, Long authorId, String content) {
        Recipe recipe = recipeRepository.get(recipeId);
        if (!recipe.getIsCommentEnabled()) {
            throw new IllegalStateException(
                    "댓글 기능이 해제된 레시피예요. 레시피 작성자가 댓글 기능을 사용하도록 설정해야 댓글을 작성할 수 있어요.");
        }
        EatzUser author = userRepository.getReference(authorId);

        Comment comment = Comment.create(author, recipe, content);
        commentRepository.save(comment);

        return new CommentBasicDto(comment);
    }

    /**
     * 댓글을 업데이트합니다.
     * @param recipeId 댓글을 달 레시피의 ID
     * @param id 댓글의 ID
     * @param authorId 댓글 업데이트를 요청한 사용자의 ID (작성자의 ID)
     * @param content 업데이트할 댓글의 내용
     * @return 업데이트된 댓글의 기본 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public CommentBasicDto update(Long recipeId, Long id, Long authorId, String content) {
        Recipe recipe = recipeRepository.get(recipeId);
        if (!recipe.getIsCommentEnabled()) {
            throw new IllegalStateException(
                    "댓글 기능이 해제된 레시피예요. 레시피 작성자가 댓글 기능을 사용하도록 설정해야 댓글을 수정할 수 있어요.");
        }

        userRepository.validateExists(authorId);

        Comment comment = commentRepository.get(id);
        comment.updateContent(authorId, content);
        return new CommentBasicDto(comment);
    }

    /**
     * 댓글을 삭제 처리합니다.
     * <p>
     *     댓글의 작성자 뿐 아니라, 댓글이 달린 레시피의 작성자 또는, 권리자 역할이 있는 사용자도 삭제 처리할 수 있습니다.
     * </p>
     * @param id 삭제 처리할 댓글의 ID
     * @param userId 댓글 삭제 처리를 요청한 사용자의 ID (작성자의 ID)
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAsDeleted(Long id, Long userId) {
        EatzUser user = userRepository.get(userId);
        Comment comment = commentRepository.getWithRecipe(id);
        comment.markAsDeleted(user);
    }

}
