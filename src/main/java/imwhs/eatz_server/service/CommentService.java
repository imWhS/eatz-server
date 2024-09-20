package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Comment;
import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.exception.CommentNotFoundException;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
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
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("id가 " + commentId + "인 댓글이 존재하지 않습니다."));

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new IllegalArgumentException("댓글을 등록한 사용자가 아닙니다.");
        }

        comment.updateContent(content);
    }

}
