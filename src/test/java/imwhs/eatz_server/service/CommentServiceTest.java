package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.recipe.Comment;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.comment.CommentByRecipeResponseDto;
import imwhs.eatz_server.dto.comment.CommentUserDto;
import imwhs.eatz_server.exception.CommentNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedEatzUserException;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@SpringBootTest
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EatzUserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    @Transactional
    void commentRegisterTest() {
        // given
        EatzUser user = EatzUser.createMember("heextory", "heextory@icloud.com", "1q2w3e4r!");
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        String commentContent = "내 맘 속에 저장~";

        // when
        Long commentId = commentService.registerComment(recipeId, commentContent);

        // then
        boolean present = commentRepository.findById(commentId).isPresent();
        Assertions.assertThat(present).isTrue();
        Comment commentFound = commentRepository.findById(commentId).get();
        Assertions.assertThat(commentFound.getContent()).isEqualTo(commentContent);
        Assertions.assertThat(commentFound.getCreatedAt()).isNotNull();
        Assertions.assertThat(commentFound.getUpdatedAt()).isEqualTo(commentFound.getCreatedAt());
        Assertions.assertThat(commentFound.getDeletedAt()).isNull();
    }

    @Test
    @Transactional
    void updateCommentTest() {
        // given
        EatzUser user = EatzUser.createMember("heextory", "heextory@icloud.com", "1q2w3e4r!");
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(
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

        // when
        commentService.updateComment(commentId, userId, commentContentAfter);
        commentRepository.flush();

        // then
        Comment editedComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾지 못했습니다."));
        Assertions.assertThat(editedComment.getContent()).isEqualTo(commentContentAfter);
        Assertions.assertThat(editedComment.getId()).isEqualTo(commentId);
        Assertions.assertThat(editedComment.getCreatedAt()).isNotNull();
        Assertions.assertThat(editedComment.getUpdatedAt()).isNotEqualTo(editedComment.getCreatedAt());
        Assertions.assertThat(editedComment.getDeletedAt()).isNull();
    }

    @Test
    @Transactional
    void updateCommentByInvalidUserTest() {
        // given
        EatzUser user = EatzUser.createMember("heextoryA", "heextory@icloud.com", "1q2w3e4r!");
        userRepository.save(user);

        Long invalidUserId = 99999L;

        Recipe recipe = Recipe.of(
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
                        commentService.updateComment(commentId, invalidUserId, commentContentAfter))
                .isInstanceOf(UnauthorizedEatzUserException.class);
    }

    @Test
    @Transactional
    void deleteCommentTest() {
        // given
        EatzUser user = EatzUser.createMember("heextoryB", "heextory@icloud.com", "1q2w3e4r!");
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(
                user,
                "Kimchi pasta",
                "https://www.naver.com/",
                "https://www.naver.com/img.png",
                "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);

        String commentContent = "내 맘 속에 저장~";

        Comment comment = new Comment(user, recipe, commentContent);
        commentRepository.save(comment);
        Long commentId = comment.getId();

        // when
        commentService.deleteComment(commentId, userId);

        // then
        Assertions.assertThat(commentRepository.findByIdAndDeletedAtIsNull(commentId)).isEmpty();
    }

    @Test
    @Transactional
    void deleteCommentByInvalidUserTest() {
        // given
        EatzUser user = EatzUser.createMember("heextoryC", "heextory@icloud.com", "1q2w3e4r!");
        userRepository.save(user);
        Long invalidUserId = 99999L;

        Recipe recipe = Recipe.of(
                user,
                "Kimchi pasta",
                "https://www.naver.com/",
                "https://www.naver.com/img.png",
                "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);

        String commentContent = "내 맘 속에 저장~";

        Comment comment = new Comment(user, recipe, commentContent);
        commentRepository.save(comment);
        Long commentId = comment.getId();

        // when, then
        Assertions.assertThatThrownBy(() ->
                commentService.deleteComment(commentId, invalidUserId))
                .isInstanceOf(UnauthorizedEatzUserException.class);
    }

    @Test
    @Transactional
    void findCommentByIdTest() {
        // given
        EatzUser user = EatzUser.createMember(
                "heextoryAA",
                "heextory@icloud.com",
                "1q2w3e4r!");
        userRepository.save(user);

        Recipe recipe = Recipe.of(
                user,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);

        String content = "이런 존맛 레시피 발견한 나 럭키비키쟌앙~";

        Comment comment = new Comment(user, recipe, content);
        commentRepository.save(comment);

        // when
        CommentByRecipeResponseDto commentDto = commentService.findComment(comment.getId());

        // then
        Assertions.assertThat(commentDto).isNotNull();
        Assertions.assertThat(commentDto.getId()).isEqualTo(comment.getId());
        Assertions.assertThat(commentDto.getContent()).isEqualTo(comment.getContent());
        Assertions.assertThat(commentDto.getUser()).isEqualTo(new CommentUserDto(comment.getUser()));
    }

    @Test
    @Transactional
    void findCommentsByUserAndRecipeTest() {
        // given
        EatzUser user = EatzUser.createMember(
                "heextoryBB",
                "heextory@icloud.com",
                "1q2w3e4r!");
        userRepository.save(user);

        Recipe recipe = Recipe.of(
                user,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);

        String content1 = "이런 존맛 레시피 발견한 나 럭키비키쟌앙~";
        Comment comment1 = new Comment(user, recipe, content1);
        commentRepository.save(comment1);

        String content2 = "이거 완전 별루.. 내 맘 속의 별루,,,,,,";
        Comment comment2 = new Comment(user, recipe, content2);
        commentRepository.save(comment2);

        // when
        Page<CommentByRecipeResponseDto> comments = commentService.findComments(
                user.getId(),
                recipe.getId(),
                null,
                null);

        // then
        Assertions.assertThat(comments.getTotalElements()).isEqualTo(2);
    }

}
