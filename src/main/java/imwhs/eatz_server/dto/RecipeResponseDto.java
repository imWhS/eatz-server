package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.Recipe;
import lombok.Data;

@Data
public class RecipeResponseDto {

    private String title;

    private String description;

    private String url;

    private String imageUrl;

    public RecipeResponseDto(Recipe recipe) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
    }

}
