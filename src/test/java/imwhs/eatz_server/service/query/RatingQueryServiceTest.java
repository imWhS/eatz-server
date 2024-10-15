package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.rating.RatingRecipeDto;
import imwhs.eatz_server.dto.rating.RatingResponseDto;
import imwhs.eatz_server.dto.rating.RatingUserDto;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@SpringBootTest
class RatingQueryServiceTest {

    @Autowired
    RatingQueryService ratingQueryService;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    EatzUserRepository userRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @BeforeEach
    void setUp() {
        ratingRepository.deleteAll();
        userRepository.deleteAll();
        recipeRepository.deleteAll();
    }

    @Test
    @DisplayName("등록된 평가가 식별자로 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findRatingByIdTest() {
        // given
        EatzUser user = EatzUser.create("heextory1", "heextory1@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Recipe recipe = Recipe.create(
                user,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);

        int score = 4;

        Rating rating = new Rating(user, recipe, score, null);
        ratingRepository.save(rating);

        // when
        RatingResponseDto ratingDto = ratingQueryService.findRating(rating.getId());

        // then
        Assertions.assertNotNull(ratingDto);
        Assertions.assertEquals(rating.getId(), ratingDto.getId());
        Assertions.assertEquals(rating.getScore(), ratingDto.getScore());
        Assertions.assertEquals(new RatingUserDto(rating.getUser()), ratingDto.getUser());
        Assertions.assertEquals(new RatingRecipeDto(rating.getRecipe()), ratingDto.getRecipe());
    }

    @Test
    @DisplayName("등록된 평가가 평가를 등록한 사용자의 식별자와 평가가 달린 레시피의 식별자로 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findRatingByUserAndRecipeTest() {
        // given
        EatzUser user = EatzUser.create("heextory2", "heextory2@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.create(
                user,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        int score = 4;

        Rating rating = new Rating(user, recipe, score, null);
        ratingRepository.save(rating);

        // when
        RatingResponseDto ratingDto = ratingQueryService.findRating(userId, recipeId);

        // then
        Assertions.assertNotNull(ratingDto);
        Assertions.assertEquals(rating.getId(), ratingDto.getId());
        Assertions.assertEquals(rating.getScore(), ratingDto.getScore());
        Assertions.assertEquals(new RatingUserDto(rating.getUser()), ratingDto.getUser());
        Assertions.assertEquals(new RatingRecipeDto(rating.getRecipe()), ratingDto.getRecipe());
    }

    @Test
    @DisplayName("특정 레시피에 달린 모든 평가가 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findRatingsOfRecipeTest() {
        // given
        EatzUser user = EatzUser.create("heextory3", "heextory3@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Recipe recipe = Recipe.create(
                user,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        EatzUser reviewerA = EatzUser.create("awesome", "awesome@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(reviewerA);
        ratingRepository.save(new Rating(reviewerA, recipe, 5, null));

        EatzUser reviewerB = EatzUser.create("bullshit", "bullshit@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(reviewerB);
        ratingRepository.save(new Rating(reviewerB, recipe, 1, null));

        // when
        Page<RatingResponseDto> allRatings = ratingQueryService.findRatings(recipeId, null, null);

        // then
        Assertions.assertEquals(allRatings.getTotalElements(), 2);
    }

    @Test
    @DisplayName("특정 사용자가 남긴 모든 평가가 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findRatingsByUserTest() {
        // given
        EatzUser userA = EatzUser.create("heextory4", "heextory4@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userA);

        Recipe recipeA = Recipe.create(
                userA,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeA);

        EatzUser userB = EatzUser.create("curve4403", "curve4403@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userB);

        Recipe recipeB = Recipe.create(
                userB,
                "Garlic BBOKKEUMBOB",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "마늘 듬뿍 볶음밥입니당");
        recipeRepository.save(recipeB);

        EatzUser reviewer = EatzUser.create("awesome2", "awesome2@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(reviewer);

        ratingRepository.save(new Rating(reviewer, recipeA, 5, null));
        ratingRepository.save(new Rating(reviewer, recipeB, 3, null));

        // when
        Page<RatingResponseDto> ratings = ratingQueryService.findRatingsByUser(reviewer.getId(), null, null);

        // then
        Assertions.assertEquals(ratings.getTotalElements(), 2);
    }

}