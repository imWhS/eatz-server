package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.domain.SavedRecipe;
import imwhs.eatz_server.dto.savedrecipe.SavedRecipeCreateDto;
import imwhs.eatz_server.dto.savedrecipe.SavedRecipeScheduledDateUpdateDto;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.savedrecipe.SavedRecipeQueryRepository;
import imwhs.eatz_server.repository.savedrecipe.SavedRecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional(readOnly = true)
class SavedRecipeServiceTest {

    @Autowired
    private SavedRecipeService savedRecipeService;

    @Autowired
    private SavedRecipeRepository savedRecipeRepository;

    @Autowired
    private SavedRecipeQueryRepository savedRecipeQueryRepository;

    @Autowired
    private EatzUserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    @Transactional
    void saveRecipeTest() {
        // given
        EatzUser user = EatzUser.createMember("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        List<LocalDate> schedules = Arrays.asList(LocalDate.now(), LocalDate.now().minusDays(1));

        SavedRecipeCreateDto dto = new SavedRecipeCreateDto(recipeId, userId, schedules);

        // when
        Long savedRecipeId = savedRecipeService.saveRecipe(dto);

        // then
        Optional<SavedRecipe> foundSavedRecipeOpt = savedRecipeQueryRepository.findByRecipeAndUser(recipe, user);
        Assertions.assertTrue(foundSavedRecipeOpt.isPresent());

        SavedRecipe foundSavedRecipe = foundSavedRecipeOpt.get();
        Assertions.assertEquals(savedRecipeId, foundSavedRecipe.getId());
        Assertions.assertEquals(userId, foundSavedRecipe.getUser().getId());
        Assertions.assertEquals(recipeId, foundSavedRecipe.getRecipe().getId());
    }

    @Test
    @Transactional
    void findSavedRecipeByUserAndScheduledDateTest() {
        // given
        EatzUser recipeWriter = EatzUser.createMember("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(recipeWriter);
        Long recipeWriterId = recipeWriter.getId();

        EatzUser recipeSaveUser = EatzUser.createMember("heextory2", "heextory2@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(recipeSaveUser);
        Long recipeSaveUserId = recipeSaveUser.getId();

        Recipe recipeA = Recipe.of(recipeWriter, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipeA);
        Long recipeAId = recipeA.getId();

        Recipe recipeB = Recipe.of(recipeWriter, "Kimchi2 pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipeB);
        Long recipeBId = recipeB.getId();

        Recipe recipeC = Recipe.of(recipeWriter, "Kimchi3 pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipeC);
        Long recipeCId = recipeC.getId();

        LocalDate now = LocalDate.now();
        List<LocalDate> schedulesOfAB = List.of(now);
        List<LocalDate> schedulesOfC = List.of(now.plusDays(1));

        SavedRecipeCreateDto savedRecipeOfA = new SavedRecipeCreateDto(recipeAId, recipeSaveUserId, schedulesOfAB);
        SavedRecipeCreateDto savedRecipeOfB = new SavedRecipeCreateDto(recipeBId, recipeSaveUserId, schedulesOfAB);
        SavedRecipeCreateDto savedRecipeOfC = new SavedRecipeCreateDto(recipeCId, recipeSaveUserId, schedulesOfC);

        // when
        Long savedRecipeAId = savedRecipeService.saveRecipe(savedRecipeOfA);
        Long savedRecipeBId = savedRecipeService.saveRecipe(savedRecipeOfB);
        Long savedRecipeCId = savedRecipeService.saveRecipe(savedRecipeOfC);

        // then
        Optional<SavedRecipe> foundSavedRecipeA = savedRecipeRepository.findById(savedRecipeAId);
        Optional<SavedRecipe> foundSavedRecipeB = savedRecipeRepository.findById(savedRecipeBId);
        Optional<SavedRecipe> foundSavedRecipeC = savedRecipeRepository.findById(savedRecipeCId);

        Assertions.assertTrue(foundSavedRecipeA.isPresent());
        Assertions.assertTrue(foundSavedRecipeB.isPresent());
        Assertions.assertTrue(foundSavedRecipeC.isPresent());

        long savedRecipeCount = savedRecipeRepository.count();
        Assertions.assertEquals(3, savedRecipeCount);

        List<SavedRecipe> foundSavedRecipeOpt = savedRecipeQueryRepository.findByUserAndScheduledDate(recipeSaveUser, now);
        Assertions.assertEquals(2, foundSavedRecipeOpt.size());
        Assertions.assertTrue(foundSavedRecipeOpt.containsAll(Arrays.asList(foundSavedRecipeA.get(), foundSavedRecipeB.get())));
    }

    @Test
    @Transactional
    void updateScheduledDateTest() {
        // given
        EatzUser user = EatzUser.createMember("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);

        SavedRecipe savedRecipe = new SavedRecipe(recipe, user, List.of(LocalDate.now()));
        savedRecipeRepository.save(savedRecipe);
        Long savedRecipeId = savedRecipe.getId();

        LocalDate updateScheduledDate = LocalDate.now().minusDays(7);
        SavedRecipeScheduledDateUpdateDto dto = new SavedRecipeScheduledDateUpdateDto(savedRecipeId, userId, updateScheduledDate);

        // when
        savedRecipeService.updateScheduledDate(dto);

        // then
        Optional<SavedRecipe> foundSavedRecipeOpt = savedRecipeQueryRepository.findWithUserAndRecipeById(savedRecipeId);
        Assertions.assertTrue(foundSavedRecipeOpt.isPresent());
        SavedRecipe foundSavedRecipe = foundSavedRecipeOpt.get();
        Assertions.assertEquals(savedRecipeId, foundSavedRecipe.getId());
        Assertions.assertEquals(userId, foundSavedRecipe.getUser().getId());
    }

    @Test
    @Transactional
    void deleteSavedRecipeByRecipeIdTest() {
        // given
        EatzUser user = EatzUser.createMember("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        SavedRecipe savedRecipe = new SavedRecipe(recipe, user, List.of(LocalDate.now()));
        savedRecipeRepository.save(savedRecipe);
        Long savedRecipeId = savedRecipe.getId();

        // when
        savedRecipeService.deleteSavedRecipeByRecipeId(recipeId, userId);

        // then
        Assertions.assertFalse(savedRecipeRepository.existsById(savedRecipeId));
        List<SavedRecipe> foundSavedRecipes = savedRecipeRepository.findAll();
        Assertions.assertEquals(0, foundSavedRecipes.size());
    }

    @Test
    @Transactional
    void deleteSavedRecipeTest() {
        // given
        EatzUser user = EatzUser.createMember("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.of(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        SavedRecipe savedRecipe = new SavedRecipe(recipe, user, List.of(LocalDate.now()));
        savedRecipeRepository.save(savedRecipe);
        Long savedRecipeId = savedRecipe.getId();

        // when
        savedRecipeService.deleteSavedRecipe(savedRecipeId, userId);

        // then
        Assertions.assertFalse(savedRecipeRepository.existsById(savedRecipeId));
        List<SavedRecipe> foundSavedRecipes = savedRecipeRepository.findAll();
        Assertions.assertEquals(0, foundSavedRecipes.size());
    }

}