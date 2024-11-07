package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.PagedResponse;
import imwhs.eatz_server.dto.comment.CommentByRecipeResponseDto;
import imwhs.eatz_server.dto.rating.*;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.rating.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.service.RatingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Autowired
    private CommentQueryService commentQueryService;

    @Test
    @Transactional
    void findRatingDetailTest() {
        // given
        EatzUser recipeWriter = EatzUser.create("heextoryAA", "heextoryA@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(recipeWriter);

        Recipe recipe = Recipe.of(
                recipeWriter,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);

        EatzUser ratingWriter = EatzUser.create("heextoryBBB", "heextoryB@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(ratingWriter);

        Recipe recipeA = Recipe.of(
                ratingWriter,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeA);

        Recipe recipeB = Recipe.of(
                ratingWriter,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeB);

        int ratingScore = 4;

        Rating rating = new Rating(ratingWriter, recipe, ratingScore);
        ratingRepository.save(rating);

        // when
        RatingDetailResponseDto ratingDetailResponseDto = ratingQueryService.findRatingDetail(rating.getId());

        // then
        Assertions.assertNotNull(ratingDetailResponseDto);
        Assertions.assertEquals(rating.getId(), ratingDetailResponseDto.getId());
        Assertions.assertEquals(ratingWriter.getId(), ratingDetailResponseDto.getUser().getId());
        Assertions.assertEquals(2, ratingDetailResponseDto.getUser().getRecipeCount());
        Assertions.assertEquals(recipe.getId(), ratingDetailResponseDto.getRecipe().getId());
        Assertions.assertEquals(recipe.getTitle(), ratingDetailResponseDto.getRecipe().getTitle());
        Assertions.assertEquals(rating.getScore(), ratingDetailResponseDto.getScore());
    }

    @Test
    @Transactional
    void findRatingsByRecipeTest() {
        // given
        EatzUser recipeWriter = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(recipeWriter);

        Recipe recipe = Recipe.of(
                recipeWriter,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        EatzUser ratingWriterA = EatzUser.create("ratingWriterA", "heextoryA@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(ratingWriterA);

        Rating ratingA = new Rating(ratingWriterA, recipe, 4);
        ratingRepository.save(ratingA);
        RatingByRecipeResponseDto ratingAResponseDto = new RatingByRecipeResponseDto(ratingA);

        EatzUser ratingWriterB = EatzUser.create("ratingWriterB", "heextoryB@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(ratingWriterB);

        Rating ratingB = new Rating(ratingWriterB, recipe, 4);
        ratingRepository.save(ratingB);
        RatingByRecipeResponseDto ratingBResponseDto = new RatingByRecipeResponseDto(ratingB);

        // when
        PagedResponse<RatingByRecipeResponseDto> pagedRatings = ratingQueryService.findRatingsByRecipe(recipeId, null, null);
        Assertions.assertEquals(1, pagedRatings.getTotalPages());
        Assertions.assertEquals(2, pagedRatings.getTotalItems());
        List<RatingByRecipeResponseDto> ratings = pagedRatings.getData();
        Assertions.assertEquals(2, ratings.size());
        Assertions.assertTrue(ratings.contains(ratingAResponseDto));
        Assertions.assertTrue(ratings.contains(ratingBResponseDto));
    }

}