package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingWithRecipeDto {

    private Long id;

    private RecipeBasicDto recipe;

    private Integer score;

    private String content;

}
