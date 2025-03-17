package imwhs.eatz_server.dto.recipe;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * CreateRecipeDto 클래스입니다.<br/>
 * 레시피를 생성하기 위한 요청 데이터를 담는 DTO입니다.
 */
@Data
@AllArgsConstructor
public class CreateRecipeDto {

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
