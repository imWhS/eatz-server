package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

/**
 * NScheduledRecipe 클래스입니다.<br/>
 * 날짜를 지정한 레시피 정보를 저장, 관리하는 클래스입니다.<br/>
 * 사용자가 레시피를 플래너에 추가하면 생성됩니다.
 */
@Getter
@Entity
public class NScheduledRecipe {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private EatzUser user;

    private LocalDate date;

    private Integer priority = null;

    public static NScheduledRecipe of(Recipe recipe, EatzUser user, LocalDate date, Integer priority) {
        NScheduledRecipe scheduledRecipe = new NScheduledRecipe();
        scheduledRecipe.recipe = recipe;
        scheduledRecipe.user = user;
        scheduledRecipe.date = date;
        scheduledRecipe.priority = priority;
        return scheduledRecipe;
    }

}
