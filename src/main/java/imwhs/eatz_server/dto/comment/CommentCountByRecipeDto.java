package imwhs.eatz_server.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentCountByRecipeDto {

    private Long recipeId;

    private Long commentCount;

}
