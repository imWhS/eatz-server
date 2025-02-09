package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.recipe.RecipeDto;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.service.recipe.RecipeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional(readOnly = true)
class RecipeQueryServiceTest {

    @Autowired
    private RecipeQueryService recipeQueryService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private EatzUserRepository userRepository;

    @Autowired
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        recipeRepository.deleteAll();
    }

    @Test
    @DisplayName("등록된 모든 레시피가 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findAllRecipesTest() {
        // given
        EatzUser user = EatzUser.createMember(
                "heextory2",
                "heextory2@icloud.com",
                "1q2w3e4r!");
        userRepository.save(user);

        Recipe recipeA = Recipe.of(
                user,
                "Kimchi pasta",
                "https://www.naver.com",
                "https://imgcdn.naver.com",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeA);
        Recipe recipeB = Recipe.of(
                user,
                "Kimchi pasta",
                "https://www.naver.com",
                "https://imgcdn.naver.com",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeB);
        Recipe recipeC = Recipe.of(
                user,
                "Kimchi pasta",
                "https://www.naver.com",
                "https://imgcdn.naver.com",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeC);

        Recipe deletedRecipe = Recipe.of(
                user,
                "Kimchi pasta",
                "https://www.naver.com",
                "https://imgcdn.naver.com",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(deletedRecipe);
        recipeService.deleteRecipe(deletedRecipe.getId(), user.getId());

        // when
        Page<RecipeDto> recipes = recipeQueryService.findAllRecipes(null);

        // then
        Assertions.assertThat(recipes.get().count()).isEqualTo(3);
    }

    @Test
    @DisplayName("특정 사용자가 등록한 모든 레시피가 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findAllRecipesByUserTest() {
        // given
        EatzUser user = EatzUser.createMember(
                "heextory3",
                "heextory3@icloud.com",
                "1q2w3e4r!");
        userRepository.save(user);
        Long userId = user.getId();

        EatzUser anotherUser = EatzUser.createMember(
                "yourstory",
                "mystory@icloud.com",
                "1q2w3e4r!");
        userRepository.save(anotherUser);

        Recipe recipeA = Recipe.of(
                user,
                "Kimchi pasta",
                "https://www.naver.com",
                "https://imgcdn.naver.com",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeA);
        Recipe recipeB = Recipe.of(
                user,
                "Kimchi pasta",
                "https://www.naver.com",
                "https://imgcdn.naver.com",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeB);
        Recipe recipeC = Recipe.of(
                user,
                "Kimchi pasta",
                "https://www.naver.com",
                "https://imgcdn.naver.com",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeC);

        Recipe anotherRecipe = Recipe.of(
                anotherUser,
                "Kimchi pasta",
                "https://www.naver.com",
                "https://imgcdn.naver.com",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(anotherRecipe);

        // when
//        Page<RecipeDto> recipes = recipeQueryService.findAllRecipesByUser(userId, null, null);

        // then
//        Assertions.assertThat(recipes.get().count()).isEqualTo(3);
    }

}