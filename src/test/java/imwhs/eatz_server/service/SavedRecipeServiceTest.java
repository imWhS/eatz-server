package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.domain.SavedRecipe;
import imwhs.eatz_server.dto.savedRecipe.SaveRecipeRequestDto;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.savedRecipe.SavedRecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SavedRecipeServiceTest {

    @Autowired
    private SavedRecipeService savedRecipeService;

    @Autowired
    private SavedRecipeRepository savedRecipeRepository;

    @Autowired
    private EatzUserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    void saveRecipeTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        SaveRecipeRequestDto dto = new SaveRecipeRequestDto(recipeId, userId, LocalDate.now());

        // when
        Long savedRecipeId = savedRecipeService.saveRecipe(dto);

        // then
        Optional<SavedRecipe> foundSavedRecipe = savedRecipeRepository.findByRecipeAndUser(recipe, user);
        Assertions.assertTrue(foundSavedRecipe.isPresent());

        SavedRecipe savedRecipe = foundSavedRecipe.get();
        Assertions.assertEquals(savedRecipeId, savedRecipe.getId());
        Assertions.assertEquals(userId, savedRecipe.getUser().getId());
        Assertions.assertEquals(recipeId, savedRecipe.getRecipe().getId());

    }

}