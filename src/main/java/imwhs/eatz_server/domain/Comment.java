package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue
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

    public void updateContent(String content) {
        this.content = content;
    }

}
