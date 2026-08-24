package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "blocked", uniqueConstraints = {
        @UniqueConstraint(name = "uk_blocker_blocked_user", columnNames = {"blocker_id", "blocked_user_id"})
})
@Entity
public class Blocked extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 차단한 사용자 (차단을 요청한 사용자)
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 연관 관계인 EatzUser 레코드가 삭제되면,
     *          데이터베이스를 통해 해당 EatzUser의 ID가 외래 키인 Block 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    @EqualsAndHashCode.Include
    private EatzUser blocker;

    /**
     * 차단된 사용자 (차단 하려는 사용자)
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 연관 관계인 EatzUser 레코드가 삭제되면,
     *          데이터베이스를 통해 해당 EatzUser의 ID가 외래 키인 Block 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_user_id", nullable = false)
    @EqualsAndHashCode.Include
    private EatzUser blockedUser;

    public static Blocked create(EatzUser blocker, EatzUser blockedUser) {
        validateBlocker(blocker);
        validateBlockedUser(blockedUser);

        return new Blocked(null, blocker, blockedUser);
    }

    private static void validateBlockedUser(EatzUser blockedUser) {
        if (blockedUser == null) {
            throw new EatzInvalidRequestArgumentException("필수 항목인 차단된 사용자가 비어 있어요.");
        }
    }

    private static void validateBlocker(EatzUser blocker) {
        if (blocker == null) {
            throw new EatzInvalidRequestArgumentException("필수 항목인 차단을 요청한 사용자가 비어 있어요.");
        }
    }

}
