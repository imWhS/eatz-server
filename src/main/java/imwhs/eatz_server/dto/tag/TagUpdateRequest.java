package imwhs.eatz_server.dto.tag;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 태그의 업데이트 요청을 하기 위해 필요한 정보를 포함합니다.
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class TagUpdateRequest {

    /**
     * 업데이트할 태그의 이름
     * <ul>
     *     <li> 필수 항목입니다. </li>
     * </ul>
     */
    @NotNull
    private String name;

    /**
     * 업데이트할 태그의 키워드
     */
    private String keyword;

    /**
     * 업데이트할 태그의 설명
     */
    private String description;

    /**
     * 업데이트할 태그의 이모지
     */
    private String emoji;

}
