package imwhs.eatz_server.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.util.Objects;

@Data
public class RecipeItemBasicDto {

    private Long id;

    private String title;

    private String imageUrl;

    /**
     * 요리 시간. '분' 단위를 사용합니다.
     */
    private Integer cookingTime;

    /**
     * 준비 시간. '분' 단위를 사용합니다.
     */
    private Integer prepTime;

    /**
     *  레시피에 달린 평가 수
     */
    private Long ratingCount;

    /**
     * 레시피에 달린 평가들의 평균 점수.
     * 1~5점 사이의 자연수 값이며, 사용자들이 남긴 평가 점수의 평균입니다.
     */
    private Double averageRatingScore;

    public RecipeItemBasicDto(Long id, String title, String imageUrl, Long cookingTime, Long prepTime, Long ratingCount, Double averageRatingScore) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.cookingTime = toMinutes(cookingTime);
        this.prepTime = toMinutes(prepTime);
        this.ratingCount = ratingCount;
        this.averageRatingScore = averageRatingScore;
    }

    private Integer toMinutes(Long seconds) {
        return (seconds == null) ? null : (int) (seconds / 60);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeItemBasicDto that = (RecipeItemBasicDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Duration getCookingTimeAsDuration() {
        return Duration.ofMinutes(cookingTime);
    }

    public Duration getPrepTimeAsDuration() {
        return Duration.ofMinutes(prepTime);
    }

}
