package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

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

    public Recipe toEntity(EatzUser user) {
        return Recipe.of(user, title, url, imageUrl, description);
    }

}
