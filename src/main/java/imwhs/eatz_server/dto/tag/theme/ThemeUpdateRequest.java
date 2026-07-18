package imwhs.eatz_server.dto.tag.theme;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 테마의 업데이트 요청을 하기 위해 필요한 정보를 포함합니다.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ThemeUpdateRequest {

    @NotBlank
    private String name;

    private String description;

}
