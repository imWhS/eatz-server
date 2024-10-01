package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentRecipeDto {

    private Long id;

    private String title;

    private String description;

    public CommentRecipeDto(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.description = recipe.getDescription();
    }

}
