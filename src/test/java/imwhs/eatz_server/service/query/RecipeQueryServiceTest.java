package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.recipe.RecipeResponseDto;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.service.RecipeService;
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
    @DisplayName("등록된 레시피가 식별자로 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findRecipeByIdTest() {
        // given
        EatzUser user = EatzUser.create("heextory1", "heextory1@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Recipe recipe = Recipe.create(user, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        // when
        RecipeResponseDto foundRecipe = recipeQueryService.findRecipeById(recipeId);

        // then
        Assertions.assertThat(foundRecipe.getId()).isEqualTo(recipeId);
        Assertions.assertThat(foundRecipe.getTitle()).isEqualTo(recipe.getTitle());
        Assertions.assertThat(foundRecipe.getDescription()).isEqualTo(recipe.getDescription());
        Assertions.assertThat(foundRecipe.getUrl()).isEqualTo(recipe.getUrl());
        Assertions.assertThat(foundRecipe.getImageUrl()).isEqualTo(recipe.getImageUrl());
    }

    @Test
    @DisplayName("등록된 모든 레시피가 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findAllRecipesTest() {
        // given
        EatzUser user = EatzUser.create("heextory2", "heextory2@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Recipe recipeA = Recipe.create(user, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeA);
        Recipe recipeB = Recipe.create(user, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeB);
        Recipe recipeC = Recipe.create(user, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeC);

        Recipe deletedRecipe = Recipe.create(user, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(deletedRecipe);
        recipeService.deleteRecipe(deletedRecipe.getId(), user.getId());

        // when
        Page<RecipeResponseDto> recipes = recipeQueryService.findAllRecipes(null, null);

        // then
        Assertions.assertThat(recipes.get().count()).isEqualTo(3);
    }

    @Test
    @DisplayName("특정 사용자가 등록한 모든 레시피가 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findAllRecipesByUserTest() {
        // given
        EatzUser user = EatzUser.create("heextory3", "heextory3@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        EatzUser anotherUser = EatzUser.create("yourstory", "mystory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(anotherUser);

        Recipe recipeA = Recipe.create(user, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeA);
        Recipe recipeB = Recipe.create(user, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeB);
        Recipe recipeC = Recipe.create(user, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeC);

        Recipe anotherRecipe = Recipe.create(anotherUser, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(anotherRecipe);

        // when
        Page<RecipeResponseDto> recipes = recipeQueryService.findAllRecipesByUser(userId, null, null);

        // then
        Assertions.assertThat(recipes.get().count()).isEqualTo(3);
    }

}