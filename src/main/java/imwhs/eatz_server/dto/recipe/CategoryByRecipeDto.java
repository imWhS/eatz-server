package imwhs.eatz_server.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryByRecipeDto {

    private Long recipeId;

    private Long categoryId;

    private String categoryName;

}
