package imwhs.eatz_server.service.recipe;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.domain.IngredientRecipe;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Category;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.domain.recipe.RecipeCategory;
import imwhs.eatz_server.dto.recipe.CreateRecipeDto;
import imwhs.eatz_server.dto.recipe.UpdateRecipeDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import imwhs.eatz_server.repository.recipe.CategoryRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * RecipeService 클래스입니다.<br/>
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private EatzUserRepository userRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * 새 레시피를 등록합니다.
     * @param dto 등록할 레시피 정보를 담고 있는 CreateRecipeDto.
     * @return 등록 완료된 레시피 정보를 담고 있는 RecipeResponseDto.
     * @throws EatzUserNotFoundException userId에 해당하는 사용자가 존재하지 않는 경우.
     */
    @Transactional
    public Long register(CreateRecipeDto dto, String username) {
        EatzUser user = getEatzUser(username);
        Recipe recipe = Recipe.create(user, dto.getTitle(), dto.getUrl(), dto.getImageUrl(), dto.getDescription());

        // 레시피에 재료를 추가합니다.
        List<Long> ingredientIds = dto.getIngredientIds();
        addIngredients(ingredientIds, recipe);

        // 레시피에 카테고리를 추가합니다.
        List<String> categoryNames = dto.getCategoryNames();
        addCategories(categoryNames, recipe);

        // 레시피를 저장합니다.
        recipeRepository.save(recipe);

        return recipe.getId();
    }

    /**
     * 레시피를 업데이트합니다.
     * @param id 업데이트할 레시피 식별자.
     * @param dto 업데이트할 레시피 정보를 담고 있는 UpdateRecipeDto.
     * @param username 레시피 업데이트를 요청한 사용자의 사용자 이름.
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우.
     * @throws UnauthorizedAccessException 레시피 삭제 처리를 요청한 사용자 식별자와 레시피를 등록한 사용자 식별자가 다른 경우.
     */
    @Transactional
    public void update(Long id, UpdateRecipeDto dto, String username) {
        Recipe recipe = getRecipe(id);
        EatzUser user = getEatzUser(username);
        validateUserAuthorization(recipe, user);

        recipe.update(dto.getTitle(), dto.getUrl(), dto.getImageUrl(), dto.getDescription());

        // 기존 레시피에 추가했던 재료를 모두 삭제하고 새 재료를 추가합니다.
        recipe.clearAllIngredientRecipes();
        List<Long> ingredientIds = dto.getIngredientIds();
        addIngredients(ingredientIds, recipe);

        // 기존 레시피에 추가했던 카테고리를 모두 삭제하고 새 카테고리를 추가합니다.
        recipe.clearAllRecipeCategories();
        List<String> categoryNames = dto.getCategoryNames();
        addCategories(categoryNames, recipe);
    }

    /**
     * 레시피를 삭제 처리합니다.
     * @param id 삭제 처리할 레시피 식별자.
     * @param username 레시피 삭제 처리를 요청한 사용자의 사용자 이름.
     * @throws RecipeNotFoundException id에 해당하는 레시피가 존재하지 않는 경우.
     * @throws UnauthorizedAccessException 레시피 삭제 처리를 요청한 사용자 식별자.와 레시피를 등록한 사용자 식별자가 다른 경우.
     */
    @Transactional
    public void markAsDeleted(Long id, String username) {
        EatzUser user = getEatzUser(username);
        Recipe recipe = getRecipe(id);
        validateUserAuthorization(recipe, user);
        recipe.markAsDeleted();
    }

    private EatzUser getEatzUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EatzUserNotFoundException(username));
    }

    private Recipe getRecipe(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(id));
    }

    private void validateUserAuthorization(Recipe recipe, EatzUser user) {
        if (!recipe.getAuthor().equals(user)) {
            throw new UnauthorizedAccessException("해당 레시피를 등록한 사용자가 아니어서, 레시피를 업데이트할 권한이 없습니다.");
        }
    }

    /**
     * 이름에 해당하는 카테고리들을 레시피에 추가합니다.<br/>
     * 이름에 해당하는 카테고리가 존재하지 않으면, 해당 카테고리를 생성 후 레시피에 추가합니다.
     * @param categoryNames 레시피에 추가하려는 카테고리 이름 목록.
     * @param recipe 레시피 엔티티.
     */
    private void addCategories(List<String> categoryNames, Recipe recipe) {
        if (categoryNames == null || categoryNames.isEmpty()) return;

        List<Category> existingCategories = categoryRepository.findByNameIn(categoryNames);
        existingCategories.forEach(category -> {
            recipe.addRecipeCategory(RecipeCategory.of(recipe, category));
        });

        List<String> existingCategoryNames = existingCategories.stream().map(Category::getName).toList();
        categoryNames.stream()
                .filter(categoryName -> !existingCategoryNames.contains(categoryName))
                .forEach(missingCategoryName -> {
                    Category missingCategory = Category.create(missingCategoryName);
                    categoryRepository.save(missingCategory);
                    recipe.addRecipeCategory(RecipeCategory.of(recipe, missingCategory));
                });
    }

    /**
     * ID에 해당하는 재료들을 레시피에 추가합니다.
     * @param ingredientIds 레시피에 추가하려는 재료 ID 목록.
     * @param recipe 레시피 엔티티.
     */
    private void addIngredients(List<Long> ingredientIds, Recipe recipe) {
        if (ingredientIds == null || ingredientIds.isEmpty()) return;

        List<Ingredient> ingredients = new ArrayList<>(ingredientRepository.findAllById(ingredientIds));
        for (Ingredient ingredient : ingredients) {
            recipe.addIngredientRecipe(IngredientRecipe.create(ingredient, recipe));
        }
    }

}
