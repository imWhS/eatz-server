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

    /*
    레시피에 재료 추가
    레시피에서 재료 삭제
    해당 재료를 사용하는 모든 레시피 조회
    레시피에 추가된 모든 재료 조회
     */


    // TODO: 중복 추가 예외 처리
    @Transactional
    public Long addIngredientToRecipe(Long ingredientId, Long recipeId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(IllegalArgumentException::new);
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));

        if (ingredientRecipeRepository.existsByRecipeIdAndIngredientId(recipeId, ingredientId)) {
            throw new IllegalArgumentException("재료(" + ingredient.getName() + ")가 이미 레시피(" + recipe.getTitle() + ")에 추가되어 있어요.");
        }

        IngredientRecipe ingredientRecipe = IngredientRecipe.create(ingredient, recipe);
        return ingredientRecipeRepository.save(ingredientRecipe).getId();
    }

    @Transactional
    public List<Long> addAllIngredientsToRecipe(List<Long> ingredientIds, Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));

        List<Long> existingIngredientIdsOfRecipe = ingredientRecipeRepository.findIngredientIdsByRecipeId(recipeId);
        System.out.println("레시피에 추가되어 있는 재료 목록");
        for (Long l : existingIngredientIdsOfRecipe) {
            System.out.println("   id = " + l);
        }

        List<Long> ingredientIdsToAdd = new ArrayList<>();

        for (Long ingredientId : ingredientIds) {
            if (!existingIngredientIdsOfRecipe.contains(ingredientId)) {
                System.out.println("레시피에 추가할 재료 ID = " + ingredientId);
                ingredientIdsToAdd.add(ingredientId);
            } else {
                System.out.println("레시피에 이미 추가돼있는 재료 ID = " + ingredientId);
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
        List<IngredientRecipe> ingredientRecipes = ingredientRecipeRepository.findByRecipeIdWithIngredient(recipeId);
        return ingredientRecipes.stream().map(
                ingredientRecipe -> new IngredientDto(
                        ingredientRecipe.getId(), ingredientRecipe.getIngredient().getName())).toList();
    }

}
