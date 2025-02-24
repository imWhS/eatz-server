package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.domain.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * RatingRecipeDto 클래스입니다.<br/>
 * 평가와 연관 관계인 레시피의 간략한 정보를 전달하기 위해 사용합니다.
 */
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
