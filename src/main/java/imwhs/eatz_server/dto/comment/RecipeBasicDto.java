package imwhs.eatz_server.dto.comment;

import imwhs.eatz_server.domain.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * CommentRecipeDto 클래스입니다.<br/>
 * 댓글과 연관 관계인 레시피의 기본(최소) 정보를 포함하는 DTO입니다.
 */
@Data
@AllArgsConstructor
public class RecipeBasicDto {

    private Long id;

    private String title;

    private String imageUrl;

    public RecipeBasicDto(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.imageUrl = recipe.getImageUrl();
    }

}
