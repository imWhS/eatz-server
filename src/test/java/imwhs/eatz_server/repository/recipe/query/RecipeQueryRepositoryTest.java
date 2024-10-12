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
        EatzUser user = EatzUser.create("heextory1", "heextory1@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Recipe recipe1 = Recipe.create(user, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe1);
        Long recipeId = recipe1.getId();

        Recipe recipe2 = Recipe.create(user, "22Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe2);

        Recipe recipe3 = Recipe.create(user, "333Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe3);

        EatzUser userB = EatzUser.create("heextory2", "heextory2@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userB);

        Comment comment1 = new Comment(userB, recipe1, "내 맘 속에 저장~");
        commentRepository.save(comment1);

        Comment comment2 = new Comment(userB, recipe1, "내 맘 속에 두 번째로 저장~");
        commentRepository.save(comment2);

        Comment comment3 = new Comment(userB, recipe1, "내 맘 속에 마지막으로 저장~");
        commentRepository.save(comment3);

        Rating rating = new Rating(userB, recipe1, 5, null);
        ratingRepository.save(rating);

        // when
        RecipeDetailResponseDto recipeDetail = recipeQueryRepository.findRecipeDetailById(recipeId);

        // then
        Assertions.assertNotNull(recipeDetail);
        Assertions.assertEquals(recipeId, recipeDetail.getId());
        Assertions.assertEquals(user.getUsername(), recipeDetail.getUser().getUsername());
        Assertions.assertEquals(3, recipeDetail.getUser().getRecipeCount());

    }

}