package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.PagedApiResponse;
import imwhs.eatz_server.dto.rating.*;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.rating.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Test
    @Transactional
    void findRatingDetailTest() {
        // given
        EatzUser recipeWriter = EatzUser.createMember(
                "heextoryAA",
                "heextoryA@icloud.com",
                "1q2w3e4r!");
        userRepository.save(recipeWriter);

        Recipe recipe = Recipe.of(
                recipeWriter,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);

        EatzUser ratingWriter = EatzUser.createMember(
                "heextoryBBB",
                "heextoryB@icloud.com",
                "1q2w3e4r!");
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
        RatingDetailDto ratingDetailDto = ratingQueryService.findRatingDetail(rating.getId());

        // then
        Assertions.assertNotNull(ratingDetailDto);
        Assertions.assertEquals(rating.getId(), ratingDetailDto.getId());
        Assertions.assertEquals(ratingWriter.getId(), ratingDetailDto.getUser().getId());
        Assertions.assertEquals(2, ratingDetailDto.getUser().getRecipeCount());
        Assertions.assertEquals(recipe.getId(), ratingDetailDto.getRecipe().getId());
        Assertions.assertEquals(recipe.getTitle(), ratingDetailDto.getRecipe().getTitle());
        Assertions.assertEquals(rating.getScore(), ratingDetailDto.getScore());
    }

    @Test
    @Transactional
    void findRatingsByRecipeTest() {
        // given
        EatzUser recipeWriter = EatzUser.createMember(
                "heextory",
                "heextory@icloud.com",
                "1q2w3e4r!");
        userRepository.save(recipeWriter);

        Recipe recipe = Recipe.of(
                recipeWriter,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        EatzUser ratingWriterA = EatzUser.createMember(
                "ratingWriterA",
                "heextoryA@icloud.com",
                "1q2w3e4r!");
        userRepository.save(ratingWriterA);

        Rating ratingA = new Rating(ratingWriterA, recipe, 4);
        ratingRepository.save(ratingA);
        RatingByRecipeDto ratingAResponseDto = new RatingByRecipeDto(ratingA);

        EatzUser ratingWriterB = EatzUser.createMember(
                "ratingWriterB",
                "heextoryB@icloud.com",
                "1q2w3e4r!");
        userRepository.save(ratingWriterB);

        Rating ratingB = new Rating(ratingWriterB, recipe, 4);
        ratingRepository.save(ratingB);
        RatingByRecipeDto ratingBResponseDto = new RatingByRecipeDto(ratingB);

        // when
        PagedApiResponse<RatingByRecipeDto> pagedRatings = ratingQueryService.findRatingsByRecipe(
                recipeId,
                null,
                null);
        Assertions.assertEquals(1, pagedRatings.getTotalPages());
        Assertions.assertEquals(2, pagedRatings.getTotalItems());
        List<RatingByRecipeDto> ratings = pagedRatings.getData();
        Assertions.assertEquals(2, ratings.size());
        Assertions.assertTrue(ratings.contains(ratingAResponseDto));
        Assertions.assertTrue(ratings.contains(ratingBResponseDto));
    }

    @Test
    @Transactional
    public void findRatingsByUserTest() {
        // given
        EatzUser recipeWriterA = EatzUser.createMember(
                "heextoryA",
                "heextoryA@icloud.com",
                "1q2w3e4r!");
        userRepository.save(recipeWriterA);

        Recipe recipeKimchi = Recipe.of(
                recipeWriterA,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeKimchi);

        EatzUser recipeWriterB = EatzUser.createMember(
                "heextoryB",
                "heextoryB@icloud.com",
                "1q2w3e4r!");
        userRepository.save(recipeWriterB);

        Recipe recipeGarlic = Recipe.of(
                recipeWriterB,
                "Garlic BBOKKEUMBOB",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "마늘 듬뿍 볶음밥입니당");
        recipeRepository.save(recipeGarlic);

        EatzUser ratingWriter = EatzUser.createMember(
                "ratingWriter",
                "writer@icloud.com",
                "1q2w3e4r!");
        userRepository.save(ratingWriter);
        Long ratingWriterId = ratingWriter.getId();

        Rating ratingA = new Rating(ratingWriter, recipeKimchi, 4);
        ratingRepository.save(ratingA);
        RatingByUserDto ratingAResponseDto = new RatingByUserDto(ratingA);

        Rating ratingB = new Rating(ratingWriter, recipeGarlic, 1);
        ratingRepository.save(ratingB);
        RatingByUserDto ratingBResponseDto = new RatingByUserDto(ratingB);

        // when
        PagedApiResponse<RatingByUserDto> pagedRatings = ratingQueryService.findRatingsByUser(
                ratingWriterId,
                null,
                null);

        // then
        Assertions.assertEquals(1, pagedRatings.getTotalPages());
        Assertions.assertEquals(2, pagedRatings.getTotalItems());
        List<RatingByUserDto> ratings = pagedRatings.getData();
        Assertions.assertEquals(2, ratings.size());
        Assertions.assertTrue(ratings.contains(ratingAResponseDto));
        Assertions.assertTrue(ratings.contains(ratingBResponseDto));
    }

}