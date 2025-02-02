package imwhs.eatz_server.domain.recipe;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * SavedRecipeSchedule 엔티티 클래스입니다.
 * <ul>
 *     <li>사용자가 레시피를 저장할 때 함께 설정한 일정 정보를 저장, 관리하는 엔티티 클래스입니다.</li>
 *     <li>동일한 날짜를 설정하고 저장한 레시피가 여러 개일 경우, priority를 통해 우선 순위를 지정해
 *     해당 날짜가 설정된 레시피 목록의 정렬 기준으로 사용할 수 있습니다.</li>
 * </ul>
 */
@Entity
@Getter
public class SavedRecipeSchedule {

    /**
     * ID.<br/>
     * SavedRecipeSchedule의 식별자입니다.
     */
    @Id @GeneratedValue
    private Long id;

    /**
     * 저장한 레시피.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saved_recipe_id")
    private SavedRecipe savedRecipe;

    // TODO: 특정 사용자의 모든 저장된 레시피를 날짜 별로 조회할 때 성능 이슈 발생 시 EatzUser 연관 관계 설정 필드 추가 고려

    /**
     * 날짜.<br/>
     * 사용자가 레시피를 저장할 때 설정한 날짜입니다.
     */
    private LocalDate date;

    /**
     * 우선 순위.<br/>
     */
    @Setter
    private Integer priority = null;

    protected SavedRecipeSchedule() {}

    public SavedRecipeSchedule(SavedRecipe savedRecipe, LocalDate date) {
        this.savedRecipe = savedRecipe;
        this.date = date;
    }

}
