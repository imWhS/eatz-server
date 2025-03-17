package imwhs.eatz_server.dto.recipe;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
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

    /**
     * 요리 시간. '분' 단위를 사용합니다.
     */
    private Integer cookingTime;

    /**
     * 준비 시간. '분' 단위를 사용합니다.
     */
    private Integer prepTime;

    private List<Long> ingredientIds;

    private List<String> categoryNames;

    public Duration getCookingTimeAsDuration() {
        return cookingTime != null ? Duration.ofMinutes(cookingTime) : null;
    }

    public Duration getPrepTimeAsDuration() {
        return prepTime != null ? Duration.ofMinutes(prepTime) : null;
    }

}
