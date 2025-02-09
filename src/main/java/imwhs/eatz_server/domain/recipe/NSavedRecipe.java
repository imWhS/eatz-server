package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * NSavedRecipe 클래스입니다.<br/>
 * 저장한 레시피 정보를 저장, 관리하는 엔티티 클래스입니다.<br/>
 * 사용자가 레시피를 저장하면 생성됩니다.
 */
@Getter
@Entity
public class NSavedRecipe {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private EatzUser user;

    protected NSavedRecipe() {}

    public static NSavedRecipe of(Recipe recipe, EatzUser user) {
        NSavedRecipe savedRecipe = new NSavedRecipe();
        savedRecipe.recipe = recipe;
        savedRecipe.user = user;
        return savedRecipe;
    }

}
