package imwhs.eatz_server.service.recipe;

import imwhs.eatz_server.domain.recipe.Category;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.domain.recipe.RecipeCategory;
import imwhs.eatz_server.exception.CategoryNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.recipe.CategoryRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final RecipeRepository recipeRepository;

    @Transactional
    public Category register(String name, String description) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 필수 항목이에요.");
        }

        if (categoryRepository.existsByName(name)) {
            throw new CategoryNotFoundException("'" + name + "' 이름을 가진 카테고리가 이미 존재해요.");
        }

        Category category = Category.create(name, description);
        categoryRepository.save(category);

        return category;
    }

    @Transactional
    public Category register(String name, String description, Long recipeId) {
        Category category = register(name, description);
        Recipe recipe = findRecipe(recipeId);
        addRecipeToCategory(category, recipe);
        return category;
    }

    @Transactional
    public void addRecipe(Long categoryId, Long recipeId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new CategoryNotFoundException("id '" + categoryId + "'에 해당하는 카테고리가 존재하지 않아요."));
        Recipe recipe = findRecipe(recipeId);
        addRecipeToCategory(category, recipe);
    }

    private Recipe findRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new RecipeNotFoundException(recipeId));
        return recipe;
    }

    private void addRecipeToCategory(Category category, Recipe recipe) {
        RecipeCategory recipeCategory = RecipeCategory.of(recipe, category);
        category.addRecipeCategory(recipeCategory);
        categoryRepository.save(category);
    }

}
