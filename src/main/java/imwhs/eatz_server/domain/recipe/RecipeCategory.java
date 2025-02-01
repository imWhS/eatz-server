package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class RecipeCategory extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public static RecipeCategory of(Recipe recipe, Category category) {
        RecipeCategory recipeCategory = new RecipeCategory();
        recipeCategory.setRecipe(recipe);
        recipeCategory.setCategory(category);
        return recipeCategory;
    }

}
