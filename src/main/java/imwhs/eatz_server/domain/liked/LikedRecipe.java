package imwhs.eatz_server.domain.liked;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 레시피의 좋아요 정보를 정의하는 LikedRecipe 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "liked_recipe", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_recipe", columnNames = {"user_id", "recipe_id"})
})
@Entity
public class LikedRecipe extends Liked {

    /**
     * 레시피
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 사용자가 좋아하는 레시피 정보입니다. </li>
     *     <li> 연관 관계인 Recipe 레코드가 삭제되면, 해당 Recipe의 ID가 외래 키인 LikedRecipe 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    @EqualsAndHashCode.Include
    private Recipe recipe;

    private LikedRecipe(EatzUser user, Recipe recipe) {
        super(user);
        this.recipe = recipe;
    }

    public static void validateRecipe(Recipe recipe) {
        if (recipe == null) { throw new EatzInvalidRequestArgumentException("필수 항목인 레시피가 비어 있어요."); }
    }

    /**
     * LikedRecipe 엔티티 팩토리 메서드
     * <p> 좋아요가 활성화된 상태로 LikedRecipe 엔티티가 생성됩니다. </p>
     * @param user 사용자의 EatzUser 엔티티
     * @param recipe 레시피의 Recipe 엔티티
     * @return LikedRecipe 엔티티
     */
    public static LikedRecipe create(EatzUser user, Recipe recipe) {
        validateUser(user);
        validateRecipe(recipe);
        return new LikedRecipe(user, recipe);
    }

}
