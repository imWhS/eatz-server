package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.rating.RatingRecipeDto;
import imwhs.eatz_server.dto.rating.RatingDto;
import imwhs.eatz_server.dto.rating.RatingUserDto;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.rating.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@SpringBootTest
public class RatingServiceTest {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private EatzUserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    @DisplayName("레시피에 새 평가가 정상적으로 등록되는지 테스트합니다.")
    @Transactional
    void ratingRegisterTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(
                user,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        int score = 4;

        // when
        Long ratingId = ratingService.registerRating(recipeId, userId, score, null);

        // then
        Assertions.assertThat(ratingId).isNotNull();
        Optional<Rating> foundRating = ratingRepository.findById(ratingId);
        Assertions.assertThat(foundRating.isPresent()).isTrue();
        Assertions.assertThat(foundRating.get().getScore()).isEqualTo(score);

    }

    @Test
    @DisplayName("레시피에 평가를 중복 등록하려고 할 때, 예외가 발생하는지 테스트합니다.")
    @Transactional
    void ratingDuplicatedRegisterTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(
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

        // when, then
        Assertions.assertThat(ratingRepository.findAll()).hasSize(1);
        Assertions.assertThatThrownBy(() -> ratingService.registerRating(recipeId, userId, score, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("평가가 정상적으로 수정되는지 테스트합니다.")
    @Transactional
    void ratingUpdateTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(
                user,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        int score = 4;

        Rating rating = new Rating(user, recipe, score);
        ratingRepository.save(rating);
        Long ratingId = rating.getId();

        int newScore = 1;

        // when
        ratingService.updateRating(ratingId, userId, newScore, null);

        // then
        Optional<Rating> foundRating = ratingRepository.findById(ratingId);
        Assertions.assertThat(foundRating.isPresent()).isTrue();
        Assertions.assertThat(foundRating.get().getScore()).isEqualTo(newScore);
    }

    @Test
    @DisplayName("평가가 정상적으로 삭제되는지 테스트합니다.")
    @Transactional
    void ratingDeleteTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(
                user,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        int score = 4;

        Rating rating = new Rating(user, recipe, score);
        ratingRepository.save(rating);
        Long ratingId = rating.getId();

        // when
        ratingService.deleteRating(ratingId, userId);

        // then
        Assertions.assertThat(ratingRepository.findByIdAndDeletedAtIsNull(ratingId)).isEmpty();
    }


    @Test
    @DisplayName("등록된 평가가 식별자로 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findRatingByIdTest() {
        // given
        EatzUser user = EatzUser.create("heextory1", "heextory1@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Recipe recipe = Recipe.of(
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
        RatingDto ratingDto = ratingService.findRating(rating.getId());

        // then
        org.junit.jupiter.api.Assertions.assertNotNull(ratingDto);
        org.junit.jupiter.api.Assertions.assertEquals(rating.getId(), ratingDto.getId());
        org.junit.jupiter.api.Assertions.assertEquals(rating.getScore(), ratingDto.getScore());
        org.junit.jupiter.api.Assertions.assertEquals(new RatingUserDto(rating.getUser()), ratingDto.getUser());
        org.junit.jupiter.api.Assertions.assertEquals(new RatingRecipeDto(rating.getRecipe()), ratingDto.getRecipe());
    }

    @Test
    @DisplayName("등록된 평가가 평가를 등록한 사용자의 식별자와 평가가 달린 레시피의 식별자로 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findRatingByUserAndRecipeTest() {
        // given
        EatzUser user = EatzUser.create("heextory2", "heextory2@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(
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
        RatingDto ratingDto = ratingService.findRating(userId, recipeId);

        // then
        org.junit.jupiter.api.Assertions.assertNotNull(ratingDto);
        org.junit.jupiter.api.Assertions.assertEquals(rating.getId(), ratingDto.getId());
        org.junit.jupiter.api.Assertions.assertEquals(rating.getScore(), ratingDto.getScore());
        org.junit.jupiter.api.Assertions.assertEquals(new RatingUserDto(rating.getUser()), ratingDto.getUser());
        org.junit.jupiter.api.Assertions.assertEquals(new RatingRecipeDto(rating.getRecipe()), ratingDto.getRecipe());
    }

}
