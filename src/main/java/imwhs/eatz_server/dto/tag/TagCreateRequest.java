package imwhs.eatz_server.dto.tag;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 태그 생성 요청을 하기 위해 필요한 정보를 포함합니다.
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class TagCreateRequest {

    /**
     * 새 태그의 이름
     */
    @NotBlank
    private String name;

    /**
     * 새 태그의 이모지
     */
    private String emoji;

    /**
     * 새 태그의 설명
     */
    private String description;

    /**
     * 새 태그를 포함시킬 레시피의 ID
     */
    private Long recipeId;

    /**
     * 태그를 포함시킬 테마의 ID
     */
    private Long themeId;

}
