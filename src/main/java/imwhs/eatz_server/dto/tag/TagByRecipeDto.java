package imwhs.eatz_server.dto.tag;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class TagByRecipeDto {

    private Long recipeId;

    private Long tagId;

    private String tagName;

}
