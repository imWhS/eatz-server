package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.domain.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingRecipeDto {

    private Long id;

    private String title;

    private String description;

    public RatingRecipeDto(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
    }

}
