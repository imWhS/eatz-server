package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.domain.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RecipeBasicDto 클래스입니다.
 * <ul>
 *     <li>레시피에 대한 간략한 정보를 전달하기 위해 사용하는 DTO입니다.</li>
 * </ul>
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class RecipeBasicDto {

    @EqualsAndHashCode.Include
    private Long id;

    private String title;

    private String imageUrl;

    public RecipeBasicDto(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.imageUrl = recipe.getImageUrl();
    }

}
