package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private EatzUser user;

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
     * Comment의 필수 필드 초기화 생성자.
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
     * 댓글 내용을 수정합니다.
     * @param content 수정할 댓글 내용
     */
    public void updateContent(String content) {
        validateComment();
        this.content = content;
    }

    /**
     * 댓글 내용의 유효성을 검증합니다.
     */
    private void validateComment() {
        if (this.isMarkedAsDeleted()) {
            throw new IllegalStateException("삭제 처리된 댓글입니다.");
        }
    }

    /**
     * 댓글의 유효성을 검증합니다.
     * content의 값을 지웁니다.
     */
    @Override
    public void markAsDeleted() {
        super.markAsDeleted();
        this.content = null;
    }

}
