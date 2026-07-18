package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.Kitchenware;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 레시피에 추가된 도구의 정보를 정의하는 RecipeKitchenware 엔티티입니다.
 * <ul>
 *     <li> 레시피와 도구의 N:M 연관 관계를 N:1로 풀어서 매핑하는 엔티티입니다. </li>
 * </ul>
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "recipe_kitchenware", uniqueConstraints = {
        @UniqueConstraint(name = "uk_kitchenware_recipe", columnNames = {"kitchenware_id", "recipe_id"})
})
@Entity
public class RecipeKitchenware extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 도구
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 레시피에서 요구하는 도구 정보입니다. </li>
     *     <li> 도구에서의 역방향 조회가 필요하지 않으므로, RecipeKitchenware -> Kitchenware 단방향 연관 관계로 설정합니다. </li>
     *     <li> 연관 관계인 Kitchenware 레코드가 삭제되면,
     *          데이터베이스를 통해 해당 Kitchenware의 ID가 외래 키인 RecipeKitchenware 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kitchenware_id", nullable = false)
    @EqualsAndHashCode.Include
    private Kitchenware kitchenware;

    /**
     * 레시피
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 도구를 요구하는 레시피 정보입니다. </li>
     *     <li> 연관 관계인 Recipe 레코드가 삭제되면, 데이터베이스를 통해 해당 Recipe의 ID가 외래 키인
     *          RecipeKitchenware 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    @EqualsAndHashCode.Include
    private Recipe recipe;

    /**
     * RecipeKitchenware 엔티티 팩토리 메서드
     * <ul>
     *     <li> Recipe와 Kitchenware로 RecipeKitchenware 엔티티를 생성합니다. </li>
     *     <li> RecipeKitchenware는 특정 레시피에 도구를 추가하기 위해 사용하며,
     *          Recipe와 Kitchenware의 N:M 연관 관계를 N:1로 풀어 매핑하는 엔티티입니다.
     *          레시피에 도구를 추가하기 위해 RecipeKitchenware 엔티티를 생성하려면,
     *          Recipe.addKitchenware()를 호출해야 합니다.</li>
     *     <li> 도구에서 레시피로의 조회와 같이 Kitchenware -> Recipe 객체 그래프 탐색이 불필요하고,
     *          방대한 연관 관계 데이터가 컬렉션에 적재되는 것을 방지하기 위해 Recipe.kitchenware를 통한 단방향 연관 관계만 설정합니다. </li>
     * </ul>
     * @return RecipeKitchenware 엔티티
     */
    protected static RecipeKitchenware create(Recipe recipe, Kitchenware kitchenware) {
        RecipeKitchenware recipeKitchenware = new RecipeKitchenware(null, kitchenware, recipe);
        return recipeKitchenware;
    }

}
