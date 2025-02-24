package imwhs.eatz_server.dto.recipe.savedrecipe;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SavedRecipeCreateDto {

    @NotNull(message = "저장하려는 레시피의 ID는 필수 항목입니다.")
    private Long recipeId;

}
