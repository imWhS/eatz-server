package imwhs.eatz_server.service.recipe;

import imwhs.eatz_server.domain.recipe.Category;
import imwhs.eatz_server.domain.recipe.RecipeCategory;
import imwhs.eatz_server.dto.recipe.CategoryDto;
import imwhs.eatz_server.repository.recipe.RecipeCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecipeCategoryService {

    private final RecipeCategoryRepository recipeCategoryRepository;

    public List<CategoryDto> categoriesOfRecipe(Long recipeId) {
        List<RecipeCategory> recipeCategories = recipeCategoryRepository.findByRecipeIdWithCategory(recipeId);

        return recipeCategories.stream().map(
                recipeCategory -> {
                    Category category = recipeCategory.getCategory();
                    return new CategoryDto(category.getId(), category.getName());
                }).toList();
    }

}
