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

    /**
     * 재료의 ID
     */
    private Long id;

    /**
     * 상위 재료와의 커플링 여부
     */
    private boolean isParentCoupled;

    /**
     * 커플링된 상위 재료 이름
     * <ul>
     *     <li> 조회 대상 재료의 이름과 함께 접두어로 사용됩니다. </li>
     * </ul>
     */
    private String coupledParentName;

    /**
     * 조회 대상 재료의 이름
     */
    private String name;

    /**
     * 하위 재료 존재 여부
     */
    private boolean hasChildren;

    /**
     * 조회 대상 재료의 보관함 추가 여부
     */
    private boolean isOwnedByUser;

    /**
     * 조회 대상 재료를 좋아하는 사람 여부
     */
    private boolean isLikedByUser;

}
