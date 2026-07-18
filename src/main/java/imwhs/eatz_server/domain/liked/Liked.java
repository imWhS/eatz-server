package imwhs.eatz_server.domain.liked;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좋아요 정보를 정의하는 Liked 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@MappedSuperclass // 테이블로 생성하지 않고, 상속 받는 하위 클래스에게 매핑 정보만 제공합니다.
public abstract class Liked extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 좋아요 한 사용자
     * <ul>
     *     <li> 필수 항목입니다. </li>
     * </ul>
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @EqualsAndHashCode.Include
    private EatzUser user;

    /**
     * 좋아요 상태
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 기본 값인 true로 초기화합니다. </li>
     *     <li> 도메인 특성 상, 사용자가 특정 항목을 처음 '좋아요' 했을 때 Liked 엔티티가 처음 생성되며,
     *          데이터가 누락될 수 없기 때문에 wrapping 타입을 사용하지 않습니다. </li>
     * </ul>
     */
    private boolean isLiked = true;

    protected Liked(EatzUser user) {
        this.user = user;
    }

    /**
     * 좋아요 상태를 반전시킵니다.
     */
    public void toggleIsLiked() { this.isLiked = !this.isLiked; }

    /**
     * 좋아요 상태를 명시적으로 활성화합니다.
     */
    public void like() { this.isLiked = true; }

    /**
     * 좋아요 상태를 명시적으로 비활성화합니다.
     */
    public void unlike() { this.isLiked = false; }

    public static void validateUser(EatzUser user) {
        if (user == null) {
            throw new IllegalArgumentException("필수 항목인 사용자가 비어 있어요.");
        }
    }

}
