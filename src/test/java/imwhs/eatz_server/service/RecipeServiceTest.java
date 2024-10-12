package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.recipe.CreateRecipeDto;
import imwhs.eatz_server.dto.recipe.UpdateRecipeDto;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@SpringBootTest
public class RecipeServiceTest {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private EatzUserRepository userRepository;

    @Test
    @DisplayName("새 레시피가 정상적으로 등록되는지 테스트합니다.")
    @Transactional
    void registerRecipeTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        CreateRecipeDto createRecipeDto = new CreateRecipeDto(
                "Kimchi pasta",
                "https://www.naver.com",
                "https://imgcdn.naver.com",
                "맛있는 김치 파스타를 즐겨보세요!");

        // when
        Long recipeId = recipeService.registerRecipe(createRecipeDto, user.getId());

        // then
        Recipe recipe = recipeRepository.findById(recipeId).get();
        Assertions.assertThat(recipe.getTitle()).isEqualTo(createRecipeDto.getTitle());
        Assertions.assertThat(recipe.getDescription()).isEqualTo(createRecipeDto.getDescription());
        Assertions.assertThat(recipe.getUrl()).isEqualTo(createRecipeDto.getUrl());
        Assertions.assertThat(recipe.getImageUrl()).isEqualTo(createRecipeDto.getImageUrl());
    }

    @Test
    @DisplayName("레시피가 정상적으로 수정되는지 테스트합니다.")
    @Transactional
    void updateRecipeTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Recipe recipe = Recipe.create(user, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        UpdateRecipeDto updateRecipeDto = new UpdateRecipeDto(
                "Kimchi pasta",
                "https://www.naver.com",
                "https://imgcdn.naver.com",
                "맛있는 김치 파스타를 즐겨보세요!");

        // when
        recipeService.updateRecipe(recipeId, updateRecipeDto, user.getId());
        Recipe foundRecipe = recipeRepository.findById(recipeId).get();

        // then
        Assertions.assertThat(foundRecipe.getTitle()).isEqualTo(updateRecipeDto.getTitle());
        Assertions.assertThat(foundRecipe.getDescription()).isEqualTo(updateRecipeDto.getDescription());
        Assertions.assertThat(foundRecipe.getUrl()).isEqualTo(updateRecipeDto.getUrl());
        Assertions.assertThat(foundRecipe.getImageUrl()).isEqualTo(updateRecipeDto.getImageUrl());
    }

    @Test
    @DisplayName("레시피가 정상적으로 삭제 처리되는지 테스트합니다.")
    @Transactional
    void deleteRecipeTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Recipe recipe = Recipe.create(user, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        // when
        recipeService.deleteRecipe(recipeId, user.getId());

        // then
        Assertions.assertThat(recipeRepository.findById(recipeId).get().isMarkedAsDeleted()).isTrue();
    }

    @Test
    @DisplayName("권한이 없는 사용자가 레시피를 수정하려고 할 때, 관련 예외가 발생하는지 테스트합니다.")
    @Transactional
    void updateRecipeByUnauthorizedUserTest() {
        // given
        EatzUser userA = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userA);
        EatzUser userB = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userB);

        Recipe recipe = Recipe.create(userA, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        UpdateRecipeDto updateRecipeDto = new UpdateRecipeDto(
                "Kimchi pasta",
                "https://www.naver.com",
                "https://imgcdn.naver.com",
                "맛있는 김치 파스타를 즐겨보세요!");

        // when, then
        Assertions.assertThatThrownBy(() -> recipeService.updateRecipe(recipeId, updateRecipeDto, userB.getId())).isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @DisplayName("권한이 없는 사용자가 레시피를 삭제하려고 할 때, 관련 예외가 발생하는지 테스트합니다.")
    @Transactional
    void deleteRecipeByUnauthorizedUserTest() {
        // given
        EatzUser userA = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userA);
        EatzUser userB = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(userB);

        Recipe recipe = Recipe.create(userA, "Kimchi pasta", "https://www.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        // when, then
        Assertions.assertThatThrownBy(() -> recipeService.deleteRecipe(recipeId, userB.getId())).isInstanceOf(UnauthorizedAccessException.class);
    }

}
