package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 저장한 레시피 정보를 정의하는 SavedRecipe 엔티티입니다.
 * <ul>
 *     <li> 저장한 레시피는 사용자가 레시피를 저장하면 생성됩니다. </li>
 * </ul>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "saved_recipe", uniqueConstraints = {
        @UniqueConstraint(name = "uk_recipe_user", columnNames = {"recipe_id", "user_id"})
})
@Entity
public class SavedRecipe extends BaseEntity {

    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 레시피
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 사용자에 의해 저장된 레시피입니다. </li>
     *     <li> 연관 관계인 Recipe 레코드가 삭제되면,
     *          데이터베이스를 통해 해당 Recipe의 ID가 외래 키인 SavedRecipe 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    /**
     * 사용자
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 레시피를 저장한 사용자입니다. </li>
     *     <li> 연관 관계인 EatzUser 레코드가 삭제되면,
     *          데이터베이스를 통해 해당 EatzUser의 ID가 외래 키인 SavedRecipe 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private EatzUser user;

    public static void validateRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new EatzInvalidRequestArgumentException("필수 항목인 레시피가 비어 있어요.");
        }
    }

    public static void validateUser(EatzUser user) {
        if (user == null) {
            throw new EatzInvalidRequestArgumentException("필수 항목인 사용자가 비어 있어요.");
        }
    }

    /**
     * SavedRecipe 엔티티 팩토리 메서드
     * @param recipe 레시피의 Recipe 엔티티. 사용자에 의해 저장될 레시피입니다.
     * @param user 사용자의 EatzUser 엔티티. 레시피를 저장하려는 사용자입니다.
     * @return SavedRecipe 엔티티
     */
    public static SavedRecipe create(Recipe recipe, EatzUser user) {
        validateRecipe(recipe);
        validateUser(user);
        return new SavedRecipe(null, recipe, user);
    }

}
