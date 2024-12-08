package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.domain.SavedRecipe;
import imwhs.eatz_server.dto.savedRecipe.SavedRecipeCreateDto;
import imwhs.eatz_server.dto.savedRecipe.SavedRecipeScheduledDateUpdateDto;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.savedRecipe.SavedRecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

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
        userRepository.save(user); // 1: insert into eatz_user ()
        Long userId = user.getId();

        Recipe recipe = Recipe.of(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe); // 2: insert into recipe ()
        Long recipeId = recipe.getId();

        SavedRecipeCreateDto dto = new SavedRecipeCreateDto(recipeId, userId, LocalDate.now());

        // when
        Long savedRecipeId = savedRecipeService.saveRecipe(dto);

        // then
        Optional<SavedRecipe> foundSavedRecipeOpt = savedRecipeRepository.findByRecipeAndUser(recipe, user); // 7: select * from saved_recipe sr where sr.recipe_id = ? and sr.user_id = ?
        Assertions.assertTrue(foundSavedRecipeOpt.isPresent());

        SavedRecipe foundSavedRecipe = foundSavedRecipeOpt.get();
        Assertions.assertEquals(savedRecipeId, foundSavedRecipe.getId());
        Assertions.assertEquals(userId, foundSavedRecipe.getUser().getId());
        Assertions.assertEquals(recipeId, foundSavedRecipe.getRecipe().getId());
    }

    @Test
    void updateScheduledDateTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user); // 1: insert into eatz_user ()
        Long userId = user.getId();

        Recipe recipe = Recipe.of(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe); // 2: insert into recipe ()

        SavedRecipe savedRecipe = new SavedRecipe(recipe, user, LocalDate.now());
        savedRecipeRepository.save(savedRecipe);
        Long savedRecipeId = savedRecipe.getId();

        LocalDate updateScheduledDate = LocalDate.now().minusDays(7);
        SavedRecipeScheduledDateUpdateDto dto = new SavedRecipeScheduledDateUpdateDto(savedRecipeId, userId, updateScheduledDate);

        // when
        savedRecipeService.updateScheduledDate(dto);

        // then
        Optional<SavedRecipe> foundSavedRecipeOpt = savedRecipeRepository.findWithUserAndRecipeById(savedRecipeId);
        Assertions.assertTrue(foundSavedRecipeOpt.isPresent());
        SavedRecipe foundSavedRecipe = foundSavedRecipeOpt.get();
        Assertions.assertEquals(savedRecipeId, foundSavedRecipe.getId());
        Assertions.assertEquals(userId, foundSavedRecipe.getUser().getId());
        Assertions.assertEquals(updateScheduledDate, foundSavedRecipe.getScheduledDate());
    }

}