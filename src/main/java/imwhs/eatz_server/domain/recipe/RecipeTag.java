package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.Tag;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 레시피에 추가된 태그를 정의하는 RecipeTag 엔티티입니다.
 * <ul>
 *     <li> Recipe와 Tag의 N:M 연관 관계를 N:1로 풀어서 매핑하는 엔티티입니다. </li>
 * </ul>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "recipe_tag", uniqueConstraints = {
        @UniqueConstraint(name = "uk_recipe_tag", columnNames = {"recipe_id", "tag_id"})
})
@Entity
public class RecipeTag extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 레시피
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 태그를 추가한 레시피입니다. </li>
     *     <li> 연관 관계인 Recipe 레코드가 삭제되면,
     *          데이터베이스를 통해 해당 Recipe의 ID가 외래 키인 RecipeTag 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    @EqualsAndHashCode.Include
    private Recipe recipe;

    /**
     * 태그
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 레시피에 추가된 태그입니다. </li>
     *     <li> 연관 관계인 Tag 레코드가 삭제되면,
     *          데이터베이스를 통해 해당 Recipe의 ID가 외래 키인 RecipeTag 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    @EqualsAndHashCode.Include
    private Tag tag;

    /**
     * RecipeTag 엔티티 팩토리 메서드
     * <ul>
     *     <li> Recipe와 Tag로 RecipeTag 엔티티를 생성합니다. </li>
     *     <li> RecipeTag는 특정 레시피에 태그를 추가하기 위해 사용하며, Recipe와 Tag의 N:M 연관 관계를 N:1로 풀어서 매핑하는 엔티티입니다.
     *          레시피에 태그를 추가하기 위해 RecipeTag 엔티티를 생성하려면, Recipe.addTag()를 호출해야 합니다. </li>
     * </ul>
     * @param recipe 레시피의 Recipe 엔티티
     * @param tag 태그의 Tag 엔티티
     * @return RecipeTag 엔티티
     */
    protected static RecipeTag create(Recipe recipe, Tag tag) {
        validateRecipe(recipe);
        validateTag(tag);
        return new RecipeTag(null, recipe, tag);
    }

    public static void validateRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new EatzInvalidRequestArgumentException("필수 항목인 레시피가 비어 있어요.");
        }
    }

    public static void validateTag(Tag tag) {
        if (tag == null) {
            throw new EatzInvalidRequestArgumentException("필수 항목인 태그가 비어 있어요.");
        }
    }

}
