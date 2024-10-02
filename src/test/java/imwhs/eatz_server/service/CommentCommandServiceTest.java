package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Comment;
import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.exception.CommentNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedEatzUserException;
import imwhs.eatz_server.repository.CommentRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import imwhs.eatz_server.service.command.CommentCommandService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class CommentCommandServiceTest {

    @Autowired
    private CommentCommandService commentCommandService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EatzUserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    void commentRegisterTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.create(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        String commentContent = "내 맘 속에 저장~";

        // when
        Long commentId = commentCommandService.registerComment(recipeId, userId, commentContent);

        // then
        boolean present = commentRepository.findById(commentId).isPresent();
        Assertions.assertThat(present).isTrue();
        Assertions.assertThat(commentRepository.findById(commentId).get().getContent()).isEqualTo(commentContent);
    }

    @Test
    void updateCommentTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.create(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);

        String commentContentBefore = "내 맘 속에 저장~";
        String commentContentAfter = "나는 별루,, 내 맘 속의 별루,,,,";

        Comment comment = new Comment(user, recipe, commentContentBefore);
        commentRepository.save(comment);
        Long commentId = comment.getId();

        // when
        commentCommandService.updateComment(commentId, userId, commentContentAfter);

        // then
        Comment editedComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾지 못했습니다."));
        Assertions.assertThat(editedComment.getContent()).isEqualTo(commentContentAfter);
        Assertions.assertThat(editedComment.getId()).isEqualTo(commentId);
    }

    @Test
    void updateCommentByInvalidUserTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Long invalidUserId = 99999L;

        Recipe recipe = Recipe.create(
                user,
                "Kimchi pasta",
                "https://www.naver.com/",
                "https://www.naver.com/img.png",
                "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);

        String commentContentBefore = "내 맘 속에 저장~";
        String commentContentAfter = "나는 별루,, 내 맘 속의 별루,,,,";

        Comment comment = new Comment(user, recipe, commentContentBefore);
        commentRepository.save(comment);
        Long commentId = comment.getId();

        // when, then
        Assertions.assertThatThrownBy(() ->
                        commentCommandService.updateComment(commentId, invalidUserId, commentContentAfter))
                .isInstanceOf(UnauthorizedEatzUserException.class);
    }

    @Test
    void deleteCommentTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.create(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);

        String commentContent = "내 맘 속에 저장~";

        Comment comment = new Comment(user, recipe, commentContent);
        commentRepository.save(comment);
        Long commentId = comment.getId();

        // when
        commentCommandService.deleteComment(commentId, userId);

        // then
        Assertions.assertThat(commentRepository.findByIdAndDeletedAtIsNull(commentId)).isEmpty();
    }

    @Test
    void deleteCommentByInvalidUserTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long invalidUserId = 99999L;

        Recipe recipe = Recipe.create(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);

        String commentContent = "내 맘 속에 저장~";

        Comment comment = new Comment(user, recipe, commentContent);
        commentRepository.save(comment);
        Long commentId = comment.getId();

        // when, then
        Assertions.assertThatThrownBy(() ->
                commentCommandService.deleteComment(commentId, invalidUserId))
                .isInstanceOf(UnauthorizedEatzUserException.class);
    }

}
