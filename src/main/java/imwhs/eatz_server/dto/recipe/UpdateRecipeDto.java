package imwhs.eatz_server.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateRecipeDto {

    private String title;

    private String url;

    private String imageUrl;

    private String description;

}
