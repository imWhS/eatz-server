package imwhs.eatz_server.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Rating {

    @Id @GeneratedValue
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
    private int score;

    private boolean isHidden;

    protected Rating() {}

    /**
     * Rating의 필수 필드 초기화 생성자.
     * @param user 평가하는 사용자
     * @param recipe 평가할 레시피
     * @param score 평가 점수
     */
    public Rating(EatzUser user, Recipe recipe, int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("평가 점수는 1부터 5 사이의 자연수여야 합니다.");
        }

        this.user = user;
        this.recipe = recipe;
        this.score = score;
    }

    public void updateScore(int score) {
        this.score = score;
    }

}
