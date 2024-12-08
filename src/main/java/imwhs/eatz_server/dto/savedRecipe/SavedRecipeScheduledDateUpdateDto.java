package imwhs.eatz_server.dto.savedRecipe;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * SavedRecipeScheduledDateUpdateDto 클래스입니다.<br/>
 * 저장된 레시피(SavedRecipe)에 설정된 날짜를 업데이트하기 위한 정보를 전달하기 위한 DTO입니다.
 */
@Data
@AllArgsConstructor
public class SavedRecipeScheduledDateUpdateDto {

    @NotNull(message = "업데이트하려는 저장된 레시피(SavedRecipe 엔티티) ID는 필수 항목입니다.")
    private Long savedRecipeId;

    @NotNull(message = "저장된 레시피를 업데이트하려는 사용자 ID는 필수 항목입니다.")
    private Long userId;

    @NotNull(message = "업데이트하려는 날짜는 필수 항목입니다.")
    private LocalDate scheduledDate;

}
