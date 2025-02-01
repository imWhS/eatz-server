package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.domain.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * RecipeSummaryDto 클래스입니다.
 * <ul>
 *     <li>레시피의 기본 정보를 포함하는 DTO입니다.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class RecipeSummaryDto {

    private Long id;

    private String title;

    private String imageUrl;

    public RecipeSummaryDto(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.imageUrl = recipe.getImageUrl();
    }

}
