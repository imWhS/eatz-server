package imwhs.eatz_server.dto.ingredient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * IngredientUpdateDto 클래스입니다.<br/>
 * 재료를 업데이트할 정보를 전달하기 위해 사용합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientUpdateDto {

    /**
     * 업데이트하려는 재료의 식별자.<br/>
     * 필수 값입니다.
     */
    private Long id;

    /**
     * 재료의 새 이름.<br/>
     * null일 경우, 재료의 이름은 업데이트하지 않습니다.
     */
    private String name;

    /**
     * 재료의 새 카테고리 식별자.<br/>
     * null일 경우 카테고리를 해제합니다.
     */
    private Long categoryId;

    /**
     * 재료의 하위 재료 목록.<br/>
     * null이거나 비어있을 경우, 재료의 하위 재료 목록은 업데이트하지 않습니다.
     */
    private List<Long> childIds;

}
