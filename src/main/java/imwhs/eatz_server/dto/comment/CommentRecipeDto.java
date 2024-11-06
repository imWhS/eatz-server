package imwhs.eatz_server.dto.comment;

import imwhs.eatz_server.domain.Comment;
import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * CommentRecipeDto 클래스입니다.<br/>
 * 댓글과 연관 관계인 레시피의 기본(최소) 정보를 포함하는 DTO입니다.
 */
@Data
@AllArgsConstructor
public class CommentRecipeDto {

    private Long id;

    private String title;

    private String imageUrl;

    public CommentRecipeDto(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.imageUrl = recipe.getImageUrl();
    }

}
