package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.domain.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 레시피의 핵심 정보를 전달할 때 사용합니다.
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class RecipeEssentialDto {

    @EqualsAndHashCode.Include
    private Long id;

    private String title;

    private String imageUrl;

    public RecipeEssentialDto(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.imageUrl = recipe.getImageUrl();
    }

}
