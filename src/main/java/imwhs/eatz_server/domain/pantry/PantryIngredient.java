package imwhs.eatz_server.domain.pantry;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 사용자가 보관함에 추가한 재료의 정보를 정의하는 PantryIngredient 엔티티입니다.
 * <ul>
 *     <li> 사용자와 재료의 N:M 연관 관계를 N:1로 풀어서 매핑하는 엔티티입니다. </li>
 * </ul>
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "pantry_ingredient", uniqueConstraints = {
        @UniqueConstraint(name = "uk_ingredient_user", columnNames = {"ingredient_id", "user_id"})
})
@Entity
public class PantryIngredient extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 재료
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 사용자가 보관함에 추가한 재료 정보입니다. </li>
     *     <li> 재료에서의 역방향 조회가 필요하지 않으므로, PantryIngredient -> Ingredient 단방향 연관 관계로 설정합니다. </li>
     *     <li> 연관 관계인 Ingredient 레코드가 삭제되면,
     *          데이터베이스를 통해 해당 Ingredient의 ID가 외래 키인 PantryIngredient 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    @EqualsAndHashCode.Include
    private Ingredient ingredient;

    /**
     * 사용자
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 재료를 보관함에 추가한 사용자 정보입니다. </li>
     *     <li> EatzUser 엔티티가 비대해지는 것을 막기 위해, PantryIngredient -> EatzUser 단방향 연관 관계로 설정합니다. </li>
     *     <li> 연관 관계인 EatzUser 레코드가 삭제되면, 데이터베이스를 통해 해당 EatzUser의 ID가 외래 키인
     *          PantryIngredient 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @EqualsAndHashCode.Include
    private EatzUser user;

    /**
     * PantryIngredient 엔티티 팩토리 메서드
     * <ul>
     *     <li> Ingredient와 EatzUser로 PantryIngredient 엔티티를 생성합니다. </li>
     *     <li> PantryIngredient는 EatzUser와의 연관 관계 주인이 되지만, EatzUser 엔티티가 비대해지는 것을 막기 위해
     *          연관 관계를 설정하지 않습니다. </li>
     * </ul>
     * @param ingredient 재료의 Ingredient 엔티티
     * @param user 사용자의 EatzUser 엔티티
     * @return PantryIngredient 엔티티
     */
    public static PantryIngredient create(Ingredient ingredient, EatzUser user) {
        PantryIngredient pantryIngredient = new PantryIngredient(null, ingredient, user);
        return pantryIngredient;
    }

}
