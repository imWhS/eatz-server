package imwhs.eatz_server.repository.recipe.query;

import imwhs.eatz_server.domain.*;
import imwhs.eatz_server.dto.recipe.RecipeDetailResponseDto;
import imwhs.eatz_server.repository.CommentRepository;
import imwhs.eatz_server.repository.RatingRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional(readOnly = true)
@SpringBootTest
class RecipeQueryRepositoryTest {

    @Autowired
    private RecipeQueryRepository recipeQueryRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private EatzUserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Test
    @Transactional
    void findRecipeDetailById() {
        // given: 사용자 A가 레시피를 등록합니다.
        EatzUser userA = EatzUser.create("heextory1", "heextory1@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userA);

        Recipe recipe1 = Recipe.create(userA, "Pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 파스타를 즐겨보세요!");
        recipeRepository.save(recipe1);
        Long recipeId = recipe1.getId();

        // given: 사용자 B, C, D가 여러 댓글과 평가를 남깁니다.
        EatzUser userB = EatzUser.create("heextory2", "heextory2@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userB);
        EatzUser userC = EatzUser.create("heextory3", "heextory3@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userC);
        EatzUser userD = EatzUser.create("heextory4", "heextory4@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userD);

        // given: 사용자 B가 3개의 댓글과 2개의 평점을 남깁니다.
        commentRepository.save(new Comment(userB, recipe1, "정말 맛있어요!"));
        commentRepository.save(new Comment(userB, recipe1, "다시 먹고 싶어요."));
        commentRepository.save(new Comment(userB, recipe1, "추천합니다."));
        ratingRepository.save(new Rating(userB, recipe1, 5, "최고의 맛!"));
        ratingRepository.save(new Rating(userB, recipe1, 4, "조금 짰어요."));

        // given: 사용자 C가 2개의 댓글과 1개의 평점을 남깁니다.
        commentRepository.save(new Comment(userC, recipe1, "이 레시피는 진짜 최고입니다."));
        commentRepository.save(new Comment(userC, recipe1, "한 번 더 만들어볼게요."));
        ratingRepository.save(new Rating(userC, recipe1, 5, "맛있고 간단해요!"));

        // given: 사용자 D가 1개의 댓글과 1개의 평점을 남깁니다.
        commentRepository.save(new Comment(userD, recipe1, "아주 맛있습니다!"));
        ratingRepository.save(new Rating(userD, recipe1, 3, "무난한 맛이네요."));

        // when
        RecipeDetailResponseDto recipeDetail = recipeQueryRepository.findRecipeDetailById(recipeId);

        // then
        assertNotNull(recipeDetail);
        assertEquals(recipeId, recipeDetail.getId());
        assertEquals("Pasta", recipeDetail.getTitle());
        assertEquals(6, recipeDetail.getCommentCount());
        assertEquals(4, recipeDetail.getRatingSummary().getRatingCount());
        assertEquals(4.25, recipeDetail.getRatingSummary().getAverageRatingScore(), 0.01);
    }

}