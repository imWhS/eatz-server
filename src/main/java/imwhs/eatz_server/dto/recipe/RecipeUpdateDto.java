package imwhs.eatz_server.dto.recipe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

/**
 * 레시피의 업데이트 요청을 하기 위해 필요한 정보를 포함합니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeUpdateDto {

    /**
     * 레시피의 제목
     */
    @NotBlank
    private String title;

    /**
     * 레시피의 URL
     */
    @URL @NotBlank
    private String url;

    /**
     * 레시피의 대표 이미지 URL
     */
    @URL @NotBlank
    private String imageUrl;

    /**
     * 레시피의 요리 시간. '분' 단위를 사용합니다.
     */
    @NotNull
    private Integer cookingTime;

    /**
     * 레시피의 1회 제공량
     */
    @NotNull
    private Integer servings;

    /**
     * 댓글 사용 여부
     */
    @NotNull
    private Boolean isCommentEnabled;

    /**
     * 레시피의 설명
     */
    private String description;

    /**
     * 레시피의 준비 시간. '분' 단위를 사용합니다.
     */
    private Integer prepTime;

    /**
     * 레시피에 추가할 요구 재료 ID 목록
     */
    private List<Long> ingredientIds;

    /**
     * 레시피에 추가할 요구 도구 ID 목록
     */
    private List<Long> kitchenwareIds;

    /**
     * 레시피에 추가할 태그 이름 목록
     */
    private List<String> tagNames;

    /**
     * 레시피의 크리에이터 이름
     */
    private String creatorName;

    /**
     * 레시피의 크리에이터 URL
     */
    @URL
    private String creatorUrl;

}
