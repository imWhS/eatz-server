package imwhs.eatz_server.dto.ingredient;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 재료(Ingredient)의 기본 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> Ingredient의 핵심 및 대부분의 정보와 현재 로그인 사용자의 context를 포함합니다. </li>
 *      <li> 주로 컬렉션으로 조회할 때 사용합니다. </li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class IngredientBasicDto {

    private Long id;
    private String name;
    private boolean hasChildren;
    private boolean isOwnedByUser;
    private boolean isLikedByUser;

}
