package imwhs.eatz_server.dto.tag.theme;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 태그를 테마에 추가하는 요청을 하기 위해 필요한 정보를 포함합니다.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddTagsToThemeRequest {

    @NotEmpty
    private List<Long> tagIds;

}
