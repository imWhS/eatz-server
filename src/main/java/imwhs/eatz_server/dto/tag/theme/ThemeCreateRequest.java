package imwhs.eatz_server.dto.tag.theme;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ThemeCreateRequest 클래스입니다.
 * 테마 생성 요청을 하기 위해 필요한 정보를 포함합니다.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ThemeCreateRequest {

    @NotBlank
    private String name;

    private String description;

    private List<Long> tagIds;

}
