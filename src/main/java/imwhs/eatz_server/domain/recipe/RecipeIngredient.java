package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.Ingredient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 레시피에 추가된 재료의 정보를 정의하는 RecipeIngredient 엔티티입니다.
 * <ul>
 *     <li> 레시피와 재료의 N:M 연관 관계를 N:1로 풀어서 매핑하는 엔티티입니다. </li>
 * </ul>
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "recipe_ingredient", uniqueConstraints = {
        @UniqueConstraint(name = "uk_ingredient_recipe", columnNames = {"ingredient_id", "recipe_id"})
})
@Entity
public class RecipeIngredient extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 재료
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 레시피에서 요구하는 재료 정보입니다. </li>
     *     <li> 재료에서의 역방향 조회가 필요하지 않으므로, RecipeIngredient -> Ingredient 단방향 연관 관계로 설정합니다. </li>
     *     <li> 연관 관계인 Ingredient 레코드가 삭제되면,
     *          데이터베이스를 통해 해당 Ingredient의 ID가 외래 키인 RecipeIngredient 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    @EqualsAndHashCode.Include
    private Ingredient ingredient;

    /**
     * 레시피
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 재료를 요구하는 레시피 정보입니다. </li>
     *     <li> 연관 관계인 Recipe 레코드가 삭제되면, 데이터베이스를 통해 해당 Recipe의 ID가 외래 키인
     *          RecipeIngredient 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    @EqualsAndHashCode.Include
    private Recipe recipe;

    /**
     * RecipeIngredient 엔티티 팩토리 메서드
     * <ul>
     *     <li> Recipe와 Ingredient로 RecipeIngredient 엔티티를 생성합니다. </li>
     *     <li> RecipeIngredient는 특정 레시피에 재료를 추가하기 위해 사용하며,
     *          Recipe와 Ingredient의 N:M 연관 관계를 N:1로 풀어 매핑하는 엔티티입니다.
     *          레시피에 재료를 추가하기 위해 RecipeIngredient 엔티티를 생성하려면,
     *          Recipe.addIngredient()를 호출해야 합니다.</li>
     *     <li> 재료에서 레시피로의 조회와 같이 Ingredient -> Recipe 객체 그래프 탐색이 불필요하고,
     *          방대한 연관 관계 데이터가 컬렉션에 적재되는 것을 방지하기 위해 Recipe.ingredient를 통한 단방향 연관 관계만 설정합니다. </li>
     * </ul>
     * @return RecipeIngredient 엔티티
     */
    protected static RecipeIngredient create(Recipe recipe, Ingredient ingredient) {
        RecipeIngredient recipeIngredient = new RecipeIngredient(null, ingredient, recipe);
        return recipeIngredient;
    }

}
