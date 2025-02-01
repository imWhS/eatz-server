package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * Comment 클래스입니다.
 * <p>
 *     댓글 정보를 저장, 관리하는 엔티티 클래스입니다.
 * </p>
 */
@Getter
@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    /**
     * 댓글을 등록한 사용자.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private EatzUser user;

    /**
     * 댓글이 달려 있는 레시피.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    /**
     * 댓글 내용.
     */
    private String content;

    /**
     * 숨김 여부.
     */
    private boolean isHidden;

    protected Comment() {}

    /**
     * Comment의 필수 필드 초기화 생성자입니다.
     * @param user 댓글을 작성하는 사용자
     * @param recipe 댓글을 달 레시피
     * @param content 댓글 내용
     */
    public Comment(EatzUser user, Recipe recipe, String content) {
        this.user = user;
        this.recipe = recipe;
        this.content = content;
    }

    /**
     * 댓글 내용을 업데이트합니다.
     * @param content 업데이트할 댓글 내용
     */
    public void updateContent(String content) {
        validateComment();
        this.content = content;
    }

    /**
     * 댓글의 유효성을 검증합니다.
     */
    private void validateComment() {
        if (this.isMarkedAsDeleted()) {
            throw new IllegalStateException("삭제 처리된 댓글입니다.");
        }
    }

}
