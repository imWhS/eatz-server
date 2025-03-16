package imwhs.eatz_server.dto.recipe;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UpdateRecipeDto 클래스입니다.<br/>
 * 레시피에 대해 업데이트가 필요한 정보를 전달하기 위한 DTO입니다.
 */
@Data
@AllArgsConstructor
public class UpdateRecipeDto {

    @NotNull
    private String title;

    @NotNull
    private String url;

    @NotNull
    private String imageUrl;

    private String description;

    private LocalDateTime cookingTime;

    private LocalDateTime prepTime;

    private List<Long> ingredientIds;

    private List<String> categoryNames;

}
