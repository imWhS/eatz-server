package imwhs.eatz_server.dto.recipe;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
public class RecipeItemBasicDto {

    private Long id;

    private String title;

    private String imageUrl;

    /**
     * 요리 시간.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cookingTime;

    /**
     * 준비 시간.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime prepTime;

    /**
     *  레시피에 달린 평가 수
     */
    private Long ratingCount;

    /**
     * 레시피에 달린 평가들의 평균 점수.
     * 1~5점 사이의 자연수 값이며, 사용자들이 남긴 평가 점수의 평균입니다.
     */
    private Double averageRatingScore;

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

}
