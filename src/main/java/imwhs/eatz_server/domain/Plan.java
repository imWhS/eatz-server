package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/**
 * 플랜 정보를 정의하고, 연관 데이터를 관리하는 Plan 엔티티입니다.
 * <p> 플랜은 사용자가 레시피를 자신의 플래너의 특정 날짜에 추가한 정보를 의미합니다. </p>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class Plan extends BaseEntity {

    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 레시피
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 사용자에 의해 플래너에 추가된 레시피입니다. </li>
     *     <li> 연관 관계인 Recipe 레코드가 삭제되면, 데이터베이스를 통해 해당 Recipe의 ID가 외래 키인 Plan 레코드도 일괄 삭제합니다. </li>
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
     *     <li> 레시피를 플래너에 추가한 사용자입니다. </li>
     *     <li> 연관 관계인 EatzUser 레코드가 삭제되면, 데이터베이스를 통해 해당 Recipe의 ID가 외래 키인 Plan 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private EatzUser user;

    /**
     * 플래너의 날짜
     * <ul>
     *     <li> 필수 항목입니다. </li>
     * </ul>
     */
    @NotNull
    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    /**
     * 우선 순위
     * <ul>
     *     <li> 필수 항목입니다. </li>
     * </ul>
     */
    private Integer priority;

    /**
     * Plan의 필수 필드 초기화 생성자입니다.
     */
    public Plan(Recipe recipe, EatzUser user, LocalDateTime scheduledAt, Integer priority) {
        this.recipe = recipe;
        this.user = user;
        this.scheduledAt = scheduledAt;
        this.priority = priority;
    }

    /**
     * 플래너의 날짜를 업데이트합니다.
     * @param scheduledAt 플래너의 날짜
     */
    public void updateScheduledAt(LocalDateTime scheduledAt) {
        validateScheduledAt(scheduledAt);
        this.scheduledAt = scheduledAt;
    }

    /**
     * 우선 순위를 업데이트합니다.
     * @param priority 우선 순위
     */
    public void updatePriority(Integer priority) {
        validatePriority(priority);
        this.priority = priority;
    }

    /**
     * 플랜을 업데이트합니다.
     * @param requesterId 업데이트를 요청한 사용자의 ID
     * @param scheduledAt 플래너에 추가할 날짜
     * @param priority 우선 순위
     */
    public void update(Long requesterId, LocalDateTime scheduledAt, Integer priority) {
        validate();
        validateOwner(requesterId);

        updateScheduledAt(scheduledAt);
        updatePriority(priority);
    }

    public void validateOwner(Long userId) {
        if (!user.getId().equals(userId)) {
            throw new UnauthorizedAccessException("해당 플랜을 등록한 사용자가 아니어서, 플랜의 상태를 변경할 권한이 없어요.");
        }
    }

    /**
     * 유효성을 검증합니다.
     */
    private void validate() {
        validateNotDeleted();
    }

    /**
     * 삭제 처리 여부를 검증합니다.
     */
    public void validateNotDeleted() {
        if (isMarkedAsDeleted()) { throw new IllegalStateException("삭제 처리된 플랜이에요."); }
    }


    public static void validateScheduledAt(LocalDateTime scheduledAt) {
        if (scheduledAt == null) { throw new IllegalArgumentException("플래너의 날짜는 필수 항목이에요."); }

        if (scheduledAt.toLocalDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new IllegalArgumentException("플래너의 날짜는 현재 이후의 시점이어야 해요.");
        }
    }

    public static void validatePriority(Integer priority) {
        if (priority == null || priority < 0) {
            throw new IllegalArgumentException("우선 순위는 0 이상의 정수여야 해요.");
        }
    }

    /**
     * Plan 엔티티 팩토리 메서드
     * @param recipe 레시피의 Recipe 엔티티. 사용자에 의해 플래너에 추가될 레시피입니다.
     * @param user 사용자의 EatzUser 엔티티. 레시피를 플래너에 추가하려는 사용자입니다.
     * @return Plan 엔티티
     */
    public static Plan create(Recipe recipe, EatzUser user, LocalDateTime scheduledAt, Integer priority) {
        validateScheduledAt(scheduledAt);
        validatePriority(priority);

        return new Plan(recipe, user, scheduledAt, priority);
    }

}
