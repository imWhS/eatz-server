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
    void findRecipeDetailByIdTest() {
        // given
        EatzUser userA = EatzUser.create("heextory1", "heextory1@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userA);

        Recipe recipe1 = Recipe.create(userA, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe1);
        Long recipeId = recipe1.getId();

        EatzUser userB = EatzUser.create("heextory2", "heextory2@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userB);

        // 사용자 B가 댓글 및 평가를 남김
        Comment comment1 = new Comment(userB, recipe1, "내 맘 속에 저장~");
        commentRepository.save(comment1);

        Comment comment2 = new Comment(userB, recipe1, "내 맘 속에 두 번째로 저장~");
        commentRepository.save(comment2);

        Rating rating1 = new Rating(userB, recipe1, 5, "최근 먹은 것 중 최고의 한 끼!");
        ratingRepository.save(rating1);

        // 사용자 C가 recipe1에 추가 평가를 남김
        EatzUser userC = EatzUser.create("heextory3", "heextory3@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userC);

        Rating rating2 = new Rating(userC, recipe1, 4, "맛있지만 조금 짰어요.");
        ratingRepository.save(rating2);

        // 사용자 D가 recipe1에 추가 평가를 남김
        EatzUser userD = EatzUser.create("heextory4", "heextory4@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userD);

        Rating rating3 = new Rating(userD, recipe1, 3, "쏘 쏘 ㅋ");
        ratingRepository.save(rating3);

        // when
        RecipeDetailResponseDto recipeDetail = recipeQueryRepository.findRecipeDetailById(recipeId);

        // then
        assertNotNull(recipeDetail);
        assertEquals(recipeId, recipeDetail.getId());
        assertEquals("Kimchi pasta", recipeDetail.getTitle());
        assertEquals(2, recipeDetail.getCommentCount());
        assertEquals(3, recipeDetail.getRatingSummary().getRatingCount());
        assertEquals(4, recipeDetail.getRatingSummary().getAverageRatingScore(), 0.01);
    }

}