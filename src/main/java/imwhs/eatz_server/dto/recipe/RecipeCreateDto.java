package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * RecipeCreateDto 클래스입니다.<br/>
 * 레시피를 생성하기 위한 요청 데이터를 담는 DTO입니다.
 */
@Data
@AllArgsConstructor
public class RecipeCreateDto {

    @NotNull
    private String title;

    @NotNull
    private String url;

    @NotNull
    private String imageUrl;

    private String description;

    private List<Long> ingredientIds;

    private List<String> categoryNames;

}
