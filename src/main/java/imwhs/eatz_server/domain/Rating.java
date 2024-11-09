package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * Rating 클래스입니다.
 * <p>
 *     평가 정보를 저장, 관리하는 엔티티 클래스입니다.
 * </p>
 */
@Getter
@Entity
public class Rating extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "rating_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private EatzUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    /**
     * 평가 점수.
     * 평가 점수는 1부터 5 사이의 자연수로만 설정할 수 있습니다.
     */
    private Integer score = null;

    /**
     * 평가 내용.
     */
    private String content = null;

    /**
     * 숨김 여부.
     */
    private boolean isHidden;

    protected Rating() {}

    /**
     * Rating의 필드 초기화 생성자.
     * @param user 평가하는 사용자
     * @param recipe 평가할 레시피
     * @param score 평가 점수
     * @param content 평가 내용
     */
    public Rating(EatzUser user, Recipe recipe, int score, String content) {
        validateScore(score);

        this.user = user;
        this.recipe = recipe;
        this.score = score;
        this.content = content;
    }

    /**
     * Rating의 필수 필드 초기화 생성자.
     * @param user 평가하는 사용자
     * @param recipe 평가할 레시피
     * @param score 평가 점수
     */
    public Rating(EatzUser user, Recipe recipe, int score) {
        this(user, recipe, score, null);
    }

    /**
     * 평가 점수를 수정합니다.
     * @param score 수정할 평가 점수
     */
    public void updateScore(int score) {
        validateRating();
        validateScore(score);

        this.score = score;
    }

    /**
     * 평가 내용을 수정합니다.
     * @param content 수정할 평가 내용
     */
    public void updateContent(String content) {
        validateRating();
        this.content = content;
    }

    /**
     * 평가를 삭제 처리합니다.
     * score, content의 값을 지웁니다.
     */
    @Override
    public void markAsDeleted() {
        super.markAsDeleted();
        this.score = null;
        this.content = null;
    }

    /**
     * 평가 점수의 유효성을 검증합니다.
     * @param score 검증할 평가 점수
     */
    private static void validateScore(int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("평가 점수는 1부터 5 사이의 자연수여야 합니다.");
        }
    }

    /**
     * 평가의 유효성을 검증합니다.
     */
    private void validateRating() {
        if (this.isMarkedAsDeleted()) {
            throw new IllegalStateException("삭제 처리된 평가입니다.");
        }
    }

}
