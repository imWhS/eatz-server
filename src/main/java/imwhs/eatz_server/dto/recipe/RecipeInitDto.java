package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RecipeInitDto {
    private String title;
    private String url;
    private String imageUrl;
    private Integer cookingTime;
    private Integer servings;
    private Boolean isCommentEnabled;
    private String description;
    private Integer prepTime;
    private String creatorName;
    private String creatorUrl;

    private List<String> ingredientNames;
    private List<String> kitchenwareNames;
    private List<String> tagNames;

    public Recipe toRecipe(EatzUser author) {
        return Recipe.create(
                author,
                title,
                url,
                imageUrl,
                cookingTime,
                servings,
                isCommentEnabled,
                description,
                prepTime,
                creatorName,
                creatorUrl);
    }
}