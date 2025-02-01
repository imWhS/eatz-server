package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * SavedRecipe 클래스입니다.
 * <ul>
 *     <li>사용자에 의해 '저장된 레시피'에 대한 정보를 저장, 관리하는 엔티티 클래스입니다.</li>
 *      <li>사용자에 의해 '저장된 레시피'를 나타냅니다. 저장된 레시피 인스턴스가 생성됐다는 것은,
 *      특정 사용자가 특정 엔티티를 저장했음을 의미합니다. 저장된 레시피는 이에 대한 정보를 담습니다.</li>
 * </ul>
 */
@Entity
@Getter
public class SavedRecipe extends BaseEntity {

    /**
     * ID.
     * <p>
     *     SavedRecipe의 식별자입니다.
     * </p>
     */
    @Id @GeneratedValue
    private Long id;

    /**
     * 레시피.
     * <ul>
     *     <li>사용자가 저장한 레시피입니다.</li>
     *     <li>Many-To-One 연관 관계를 설정합니다: 사용자는 한 번에 하나의 레시피만 저장할 수 있고, 레시피는 여러 사용자에 의해 저장되어질 수 있기에,
     *     여러 개의 저장된 레시피는 오직 하나의 레시피와 연관 관계를 가질 수 있습니다.
     *     </li>
     *     <li>DB에서 Recipe의 ID를 참조하는 외래 키 필드 이름으로 recipe_id를 사용합니다.</li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    /**
     * 레시피를 저장한 사용자.
     * <ul>
     *     <li>Many-To-One 연관 관계를 설정합니다: 사용자는 여러 레시피를 저장할 수 있기에,
     *     여러 개의 저장된 레시피는 오직 하나의 사용자와 연관 관계를 가질 수 있습니다.</li>
     *     <li>DB에서 EatzUser의 ID를 참조하는 외래 키 필드 이름으로 user_id를 사용합니다.</li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private EatzUser user;

    /**
     * 사용자가 설정한 날짜.
     * <ul>
     *     <li>사용자가 게시물을 저장할 때, 직접 설정한 날짜를 나타냅니다.</li>
     *     <li>사용자가 아무 날짜도 설정하지 않은 경우, null을 가집니다.</li>
     * </ul>
     */
//    private LocalDate scheduledDate;
    // TODO: 날짜 별 우선 순위

    /**
     * 일정.
     * <ul>
     *     <li>사용자가 레시피를 저장할 때 설정한 날짜 별 정보입니다.</li>
     *     <li>One-To-Many 연관 관계를 설정합니다: 레시피를 저장할 때, 여러 날짜를 설정할 수 있습니다.</li>
     *     <li>일정 엔티티 SavedRecipeSchedule의 생명 주기는 SavedRecipe를 따릅니다.</li>
     * </ul>
     */
    @OneToMany(mappedBy = "savedRecipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedRecipeSchedule> schedules = new ArrayList<>();

    protected SavedRecipe() {}

    public SavedRecipe(Recipe recipe, EatzUser user, List<LocalDate> scheduledDates) {
        this.recipe = recipe;
        this.user = user;

        for (LocalDate date : scheduledDates) {
            this.schedules.add(new SavedRecipeSchedule(this, date));
        }
    }

    // TODO: Collection 추가 및 Many-To-Many 연관 관계 설정

}
