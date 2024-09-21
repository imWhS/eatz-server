package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Comment;
import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.exception.*;
import imwhs.eatz_server.repository.CommentRepository;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void updateComment(Long commentId, Long userId, String content) {
        Comment comment = findComment(commentId, userId);
        comment.updateContent(content);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = findComment(commentId, userId);
        commentRepository.delete(comment);
    }

    /**
     * Comment 엔티티를 가져옵니다.
     * @param commentId 댓글 ID
     * @param userId 서비스를 요청한 사용자 ID
     * @throws CommentNotFoundException commentId에 해당하는 Comment 엔티티가 존재하지 않을 경우
     * @throws UnauthorizedEatzUserException 댓글을 등록한 사용자의 id가 userId와 일치하지 않을 경우(접근 권한이 없는 사용자의 요청인 경우)
     * @return Comment 엔티티
     */
    private Comment findComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("id가 " + commentId + "인 댓글이 존재하지 않습니다."));

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new UnauthorizedEatzUserException("댓글을 등록한 사용자가 아니어서, 수정을 진행할 수 없습니다.");
        }
        return comment;
    }

}
