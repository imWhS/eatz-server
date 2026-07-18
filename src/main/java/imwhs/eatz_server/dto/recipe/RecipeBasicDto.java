package imwhs.eatz_server.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 레시피(Recipe)의 기본 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> Recipe의 핵심 및 대부분의 정보와 Rating, Comment 등의 연관 관계 엔티티의 정보 및 현재 로그인 사용자의 context를 포함합니다. </li>
 *      <li> 주로 컬렉션으로 조회할 때 사용합니다. </li>
 * </ul>
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class RecipeBasicDto {

    private Long id;

    private String title;

    private String imageUrl;

    private Integer servings;

    /**
     * 요리 시간
     * 시간 단위로 '초'를 사용합니다.
     */
    private Integer cookingTime;

    /**
     * 준비 시간
     * 시간 단위로 '초'를 사용합니다.
     */
    private Integer prepTime;

    /**
     * 작성자의 ID
     */
    private Long authorId;

    /**
     * 작성자의 사용자 이름
     */
    private String authorUsername;

    /**
     * 레시피에 달린 평가 수
     */
    private long ratingCount;

    /**
     * 레시피에 달린 평가들의 평균 점수
     * 1~5점 사이의 자연수 값이며, 사용자들이 남긴 평가 점수의 평균입니다.
     */
    private double ratingAverageScore;

    private boolean isOwnedByUser;

    private boolean isLikedByUser;

    private boolean isSavedByUser;

}
