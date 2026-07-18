package imwhs.eatz_server.dto.recipe.explore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * '둘러보기(Explore)'에서 사용할 레시피 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> Recipe의 기본 정보와 연관 관계 엔티티의 정보 및 현재 로그인 사용자의 context를 포함합니다.</li>
 *      <li> 주로 컬렉션으로 조회할 때 사용합니다. </li>
 * </ul>
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class ExploreRecipeDto {

    /**
     * 레시피의 ID
     */
    private Long id;

    /**
     * 레시피의 제목
     */
    private String title;

    /**
     * 레시피의 대표 이미지 URL
     */
    private String imageUrl;

    /**
     * 레시피의 1회 제공량
     */
    private Integer servings;

    /**
     * 레시피의 요리 시간
     * 시간 단위로 '초'를 사용합니다.
     */
    private Integer cookingTime;

    /**
     * 레시피의 준비 시간
     * 시간 단위로 '초'를 사용합니다.
     */
    private Integer prepTime;

    /**
     * 레시피의 작성자 ID
     */
    private Long authorId;

    /**
     * 레시피의 작성자 사용자 이름
     */
    private String authorUsername;

    /**
     * 레시피의 댓글 활성화 여부
     */
    private boolean isCommentEnabled;

    /**
     * 레시피를 좋아하는 사람 수
     */
    private long likedCount;

    /**
     * 레시피에 달린 댓글 수
     */
    private long commentCount;

    /**
     * 레시피에 달린 평가 수
     */
    private long ratingCount;

    /**
     * 레시피에 달린 평가들의 평균 점수
     * 1~5점 사이의 자연수 값이며, 사용자들이 남긴 평가 점수의 평균입니다.
     */
    private double ratingAverageScore;

    /**
     * 레시피의 작성자 여부
     */
    private boolean isOwnedByUser;

    /**
     * 레시피를 좋아하는 사람 여부
     */
    private boolean isLikedByUser;

    /**
     * 레시피를 저장한 사용자 여부
     */
    private boolean isSavedByUser;

}
