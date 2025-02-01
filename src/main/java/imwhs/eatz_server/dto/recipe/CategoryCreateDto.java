package imwhs.eatz_server.dto.recipe;

import lombok.Data;

/**
 * CategoryCreateDto 클래스입니다.
 */
@Data
public class CategoryCreateDto {

    private String name;

    private String description;

    private Long recipeId;

}
