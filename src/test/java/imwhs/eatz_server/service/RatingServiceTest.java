package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RatingRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

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
    void ratingRegisterTest() {
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

        // when
        Long ratingId = ratingService.registerRating(recipeId, userId, score);

        // then
        Assertions.assertThat(ratingId).isNotNull();
        Optional<Rating> foundRating = ratingRepository.findById(ratingId);
        Assertions.assertThat(foundRating.isPresent()).isTrue();
        Assertions.assertThat(foundRating.get().getScore()).isEqualTo(score);
    }

    @Test
    @DisplayName("평가가 정상적으로 수정되는지 테스트합니다.")
    void ratingUpdateTest() {
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
        int score = 4;

        Rating rating = new Rating(user, recipe, score);
        ratingRepository.save(rating);
        Long ratingId = rating.getId();

        int newScore = 1;

        // when
        ratingService.updateRating(ratingId, userId, newScore);

        // then
        Optional<Rating> foundRating = ratingRepository.findById(ratingId);
        Assertions.assertThat(foundRating.isPresent()).isTrue();
        Assertions.assertThat(foundRating.get().getScore()).isEqualTo(newScore);
    }

    @Test
    @DisplayName("평가가 정상적으로 삭제되는지 테스트합니다.")
    void ratingDeleteTest() {
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
        int score = 4;

        Rating rating = new Rating(user, recipe, score);
        ratingRepository.save(rating);
        Long ratingId = rating.getId();

        // when
        ratingService.deleteRating(ratingId, userId);

        // then
        Assertions.assertThat(ratingRepository.existsById(ratingId)).isFalse();
    }

}
