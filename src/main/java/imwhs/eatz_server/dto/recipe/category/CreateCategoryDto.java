package imwhs.eatz_server.dto.recipe.category;

import lombok.Data;

/**
 * CreateCategoryDto 클래스입니다.
 */
@Data
public class CreateCategoryDto {

    private String name;

    private String description;

    private Long recipeId;

}
