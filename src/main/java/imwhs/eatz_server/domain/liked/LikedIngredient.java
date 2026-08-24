package imwhs.eatz_server.domain.liked;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.Ingredient;
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
 * 재료의 좋아요 정보를 정의하는 LikedIngredient 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "liked_ingredient", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_ingredient", columnNames = {"user_id", "ingredient_id"})
})
@Entity
public class LikedIngredient extends Liked {

    /**
     * 재료
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 사용자가 좋아하는 재료 정보입니다. </li>
     *     <li> 연관 관계인 Ingredient 레코드가 삭제되면, 해당 Ingredient의 ID가 외래 키인 LikedIngredient 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id",  nullable = false)
    @EqualsAndHashCode.Include
    private Ingredient ingredient;

    private LikedIngredient(EatzUser user, Ingredient ingredient) {
        super(user);
        this.ingredient = ingredient;
    }

    public static void validateIngredient(Ingredient ingredient) {
        if (ingredient == null) { throw new EatzInvalidRequestArgumentException("필수 항목인 재료가 비어 있어요."); }
    }

    /**
     * LikedIngredient 엔티티 팩토리 메서드
     * <p> 좋아요가 활성화된 상태로 LikedIngredient 엔티티가 생성됩니다. </p>
     * @param user 사용자의 EatzUser 엔티티
     * @param ingredient 재료의 Ingredient 엔티티
     * @return LikedIngredient 엔티티
     */
    public static LikedIngredient create(EatzUser user, Ingredient ingredient) {
        validateUser(user);
        validateIngredient(ingredient);
        return new LikedIngredient(user, ingredient);
    }

}
