package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.dto.recipe.RecipeEssentialDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingEssentialWithRecipeDto {

    private Long id;

    private RecipeEssentialDto recipe;

    private Integer score;

    private String content;

}
