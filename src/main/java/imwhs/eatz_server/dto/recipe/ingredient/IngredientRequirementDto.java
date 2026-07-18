package imwhs.eatz_server.dto.recipe.ingredient;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *  레시피를 요리하기 위해 필요한 재료를 전달할 때 사용하는 DTO입니다.
 *  <ul>
 *      <li> Ingredient의 핵심 정보와 현재 로그인 사용자의 context를 포함합니다. </li>
 *  </ul>
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class IngredientRequirementDto {

    private Long id;

    private String name;

    private boolean isOwnedByUser;

    private boolean isLikedByUser;

}
