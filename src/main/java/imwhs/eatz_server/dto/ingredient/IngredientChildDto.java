package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.ingredient.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * IngredientChildResponseDto 클래스입니다.<br/>
 * 조회하려는 재료의 하위 재료 정보를 전달하기 위해 사용합니다.
 */
@Data
@AllArgsConstructor
public class IngredientChildDto {

    /**
     * 재료의 식별자.
     */
    private Long childId;

    /**
     * 재료의 이름.
     */
    private String childName;

    public IngredientChildDto(Ingredient child) {
        this.childId = child.getId();
        this.childName = child.getName();
    }

}