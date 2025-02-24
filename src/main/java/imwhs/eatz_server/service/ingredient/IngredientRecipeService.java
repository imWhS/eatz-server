package imwhs.eatz_server.service.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.domain.IngredientRecipe;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.ingredient.IngredientRecipeRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class IngredientRecipeService {

    private final IngredientRecipeRepository ingredientRecipeRepository;

    private final IngredientRepository ingredientRepository;

    private final RecipeRepository recipeRepository;

    @Transactional
    public Long addIngredientToRecipe(Long ingredientId, Long recipeId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(IllegalArgumentException::new);
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));

        if (ingredientRecipeRepository.existsByRecipeIdAndIngredientId(recipeId, ingredientId)) {
            throw new IllegalArgumentException("재료(" + ingredient.getName() + ")가 " +
                    "이미 레시피(" + recipe.getTitle() + ")에 추가되어 있어요.");
        }

        IngredientRecipe ingredientRecipe = IngredientRecipe.create(ingredient, recipe);
        return ingredientRecipeRepository.save(ingredientRecipe).getId();
    }

    /**
     * 레시피에 여러 재료를 추가합니다.
     * @param ingredientIds 추가할 재료 목록.
     * @param recipeId 재료를 추가할 레시피.
     * @return 추가 완료된 재료 ID 목록.
     */
    @Transactional
    public List<Long> addAllIngredientsToRecipe(List<Long> ingredientIds, Long recipeId) {
        // ID에 해당하는 레시피를 조회합니다.
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));

        // 기존 레시피에 등록된 모든 재료를 조회합니다.
        List<Long> existingIngredientIdsByRecipe = ingredientRecipeRepository.findIngredientIdsByRecipe(recipe);

        List<Long> ingredientIdsToAdd = new ArrayList<>();
        for (Long ingredientId : ingredientIds) {
            if (!existingIngredientIdsByRecipe.contains(ingredientId)) {
                ingredientIdsToAdd.add(ingredientId);
            }
        }

        if (ingredientIdsToAdd.isEmpty()) return List.of();

        List<Ingredient> ingredients = ingredientRepository.findAllById(ingredientIdsToAdd);
        if (ingredients.isEmpty()) return List.of();

        registerIngredientRecipes(ingredients, recipe);
        return ingredientIdsToAdd;
    }

    @Transactional
    public void addAllIngredientsToNewRecipe(List<Long> ingredientIds, Recipe recipe) {
        List<Ingredient> ingredients = ingredientRepository.findAllById(ingredientIds);
        registerIngredientRecipes(ingredients, recipe);
    }


    private void registerIngredientRecipes(List<Ingredient> ingredients, Recipe recipe) {
        List<IngredientRecipe> ingredientRecipes = ingredients.stream().map(ingredient -> {
            return IngredientRecipe.create(ingredient, recipe);
        }).toList();

        ingredientRecipeRepository.saveAll(ingredientRecipes);
    }

    public List<IngredientDto> ingredientsOfRecipe(Long recipeId) {
        List<IngredientRecipe> ingredientRecipes = ingredientRecipeRepository.findIngredientsByRecipeIdWithIngredient(recipeId);
        return ingredientRecipes.stream().map(
                ingredientRecipe -> new IngredientDto(
                        ingredientRecipe.getRecipe().getId(), ingredientRecipe.getIngredient().getName())).toList();
    }

}
