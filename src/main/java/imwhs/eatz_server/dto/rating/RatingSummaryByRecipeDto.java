package imwhs.eatz_server.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 특정 레시피에 달린 평가에 대한 요약 정보를 담는 DTO 클래스입니다.
 * 주로 레시피 상세 정보와 함께, 해당 레시피에 대한 전체 평가 개수와 평균 점수를
 * 클라이언트에 전달하기 위해 사용됩니다.
 */
@Data
@AllArgsConstructor
public class RatingSummaryByRecipeDto {

    private Long recipeId;

    /**
     *  레시피에 달린 평가 수
     */
    private Long ratingCount;

    /**
     * 레시피에 달린 평가들의 평균 점수
     * 1~5점 사이의 자연수 값이며, 사용자들이 남긴 평가 점수의 평균입니다.
     */
    private Double averageRatingScore;

}
