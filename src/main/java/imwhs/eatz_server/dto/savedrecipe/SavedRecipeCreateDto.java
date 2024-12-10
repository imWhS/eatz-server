package imwhs.eatz_server.dto.savedrecipe;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * SavedRecipeCreateDto 클래스입니다.<br/>
 * 사용자가 레시피 저장을 요청했을 때, SavedRecipe 엔티티를 생성하기 위한 데이터를 담는 DTO입니다.
 */
@Data
@AllArgsConstructor
public class SavedRecipeCreateDto {

    @NotNull(message = "저장하려는 레시피 ID는 필수 항목입니다.")
    private Long recipeId;

    @NotNull(message = "레시피를 저장하려는 사용자 ID는 필수 항목입니다.")
    private Long userId;

    private List<LocalDate> schedules;

}
