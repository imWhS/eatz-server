package imwhs.eatz_server.dto.recipe.savedrecipe;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SavedRecipeDeleteDto {

    @NotNull(message = "저장 취소하려는 레시피의 ID는 필수 항목입니다.")
    private Long recipeId;

}
