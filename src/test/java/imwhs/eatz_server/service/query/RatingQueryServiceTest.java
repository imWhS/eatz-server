package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.RatingRecipeDto;
import imwhs.eatz_server.dto.RatingResponseDto;
import imwhs.eatz_server.dto.RatingUserDto;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RatingRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

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

    @Test
    @DisplayName("등록된 평가가 식별자로 정상적으로 조회되는지 테스트합니다.")
    void findRatingByIdTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
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
        RatingResponseDto ratingResponseDto = ratingQueryService.findRating(rating.getId());

        // then
        Assertions.assertNotNull(ratingResponseDto);
        Assertions.assertEquals(rating.getId(), ratingResponseDto.getId());
        Assertions.assertEquals(rating.getScore(), ratingResponseDto.getScore());
        Assertions.assertEquals(new RatingUserDto(rating.getUser()), ratingResponseDto.getUser());
        Assertions.assertEquals(new RatingRecipeDto(rating.getRecipe()), ratingResponseDto.getRecipe());
    }

    @Test
    @DisplayName("등록된 평가가 평가를 등록한 사용자의 식별자와 평가가 달린 레시피의 식별자로 정상적으로 조회되는지 테스트합니다.")
    void findRatingByUserIdAndRecipeIdTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
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
        RatingResponseDto ratingResponseDto = ratingQueryService.findRating(userId, recipeId);

        // then
        Assertions.assertNotNull(ratingResponseDto);
        Assertions.assertEquals(rating.getId(), ratingResponseDto.getId());
        Assertions.assertEquals(rating.getScore(), ratingResponseDto.getScore());
        Assertions.assertEquals(new RatingUserDto(rating.getUser()), ratingResponseDto.getUser());
        Assertions.assertEquals(new RatingRecipeDto(rating.getRecipe()), ratingResponseDto.getRecipe());
    }

    @Test
    @DisplayName("특정 레시피에 달린 모든 평가가 정상적으로 조회되는지 테스트합니다.")
    void findAllRatingsOfRecipeTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
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
        Page<RatingResponseDto> allRatings = ratingQueryService.findAllRatings(recipeId, null, null);
        Assertions.assertNotNull(allRatings);
        Assertions.assertEquals(allRatings.getTotalElements(), 2);
    }

}