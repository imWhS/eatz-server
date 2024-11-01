package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.*;
import imwhs.eatz_server.dto.comment.CommentRecipeDto;
import imwhs.eatz_server.dto.comment.CommentResponseDto;
import imwhs.eatz_server.dto.comment.CommentUserDto;
import imwhs.eatz_server.repository.CommentRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@SpringBootTest
public class CommentQueryServiceTest {

    @Autowired
    CommentQueryService commentQueryService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    EatzUserRepository userRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Test
    @Transactional
    void findCommentByIdTest() {
        // given
        EatzUser user = EatzUser.create("heextoryAA", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
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
        CommentResponseDto commentDto = commentQueryService.findComment(comment.getId());

        // then
        Assertions.assertNotNull(commentDto);
        Assertions.assertEquals(comment.getId(), commentDto.getId());
        Assertions.assertEquals(comment.getContent(), commentDto.getContent());
        Assertions.assertEquals(new CommentUserDto(comment.getUser()), commentDto.getUser());
        Assertions.assertEquals(new CommentRecipeDto(comment.getRecipe()), commentDto.getRecipe());
    }

    @Test
    @Transactional
    void findCommentsByUserAndRecipeTest() {
        // given
        EatzUser user = EatzUser.create("heextoryBB", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
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
        Page<CommentResponseDto> comments = commentQueryService.findComments(user.getId(), recipe.getId(), null, null);

        // then
        Assertions.assertEquals(comments.getTotalElements(), 2);
    }

    @Test
    @Transactional
    void findCommentsOfRecipeTest() {
        // given
        EatzUser user = EatzUser.create("heextoryCC", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Recipe recipe = Recipe.of(
                user,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        EatzUser reviewerA = EatzUser.create("awesome", "awesome@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(reviewerA);
        commentRepository.save(new Comment(reviewerA, recipe, "재료 무조건 다 갖춰야하나요? ㅠㅠ"));

        EatzUser reviewerB = EatzUser.create("bullshit", "bullshit@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(reviewerB);
        commentRepository.save(new Comment(reviewerB, recipe, "제 마음 속에 우선 찜 해둘게요!"));

        // when
        Page<CommentResponseDto> allComments = commentQueryService.findCommentsByRecipe(recipeId, null, null);

        // then
        Assertions.assertEquals(allComments.getTotalElements(), 2);
    }

    @Test
    @Transactional
    void findCommentsByUserTest() {
        // given
        EatzUser userA = EatzUser.create("heextoryDD", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userA);

        Recipe recipeA = Recipe.of(
                userA,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeA);

        EatzUser userB = EatzUser.create("curve4403", "curve4403@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userB);

        Recipe recipeB = Recipe.of(
                userB,
                "Garlic BBOKKEUMBOB",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "마늘 듬뿍 볶음밥입니당");
        recipeRepository.save(recipeB);

        EatzUser reviewer = EatzUser.create("awesome", "awesome@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(reviewer);

        commentRepository.save(new Comment(reviewer, recipeA, "재료 무조건 다 갖춰야하나요? ㅠㅠ"));
        commentRepository.save(new Comment(reviewer, recipeB, "제 마음 속에 우선 찜 해둘게요!"));

        // when
        Page<CommentResponseDto> comments = commentQueryService.findCommentsByUser(reviewer.getId(), null, null);

        // then
        Assertions.assertEquals(comments.getTotalElements(), 2);
    }

}
