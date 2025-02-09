package imwhs.eatz_server.dto.recipe;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * SaveRecipeDto 클래스입니다.<br/>
 * 레시피 저장을 취소하기 위해 사용하는 DTO입니다.
 */
@Data
public class RecipeUnsaveDto {

    @NotNull(message = "저장 취소하려는 레시피 ID는 필수 항목입니다.")
    private Long recipeId;

}
