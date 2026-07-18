package imwhs.eatz_server.dto.recipe;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 저장된 레시피 생성 요청을 하기 위해 필요한 정보를 포함합니다.
 * 레시피를 저장하기 위해 사용합니다.
 */
@Data
public class SavedRecipeCreateRequest {

    @NotNull(message = "저장하려는 레시피의 ID는 필수 항목이에요.")
    private Long recipeId;

}
