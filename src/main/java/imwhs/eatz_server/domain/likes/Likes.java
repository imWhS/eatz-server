package imwhs.eatz_server.domain.likes;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Like 클래스입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Likes extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private EatzUser user;

    @Column(nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LikesType type;

    private Boolean isLiked = true;

    public static Likes of(EatzUser user, Long entityId, LikesType type) {
        return new Likes(null, user, entityId, type, true);
    }

    public boolean toggleIsLiked() {
        isLiked = !isLiked;
        return isLiked;
    }

}
