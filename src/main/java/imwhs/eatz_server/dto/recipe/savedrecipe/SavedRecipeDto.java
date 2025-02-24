package imwhs.eatz_server.dto.recipe.savedrecipe;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SavedRecipeDto {

    Long id;

    RecipeBasicDto recipe;

    /**
     * 레시피를 저장한 날짜.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

}
