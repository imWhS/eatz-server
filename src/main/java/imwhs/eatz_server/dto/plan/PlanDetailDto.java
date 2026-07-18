package imwhs.eatz_server.dto.plan;

import imwhs.eatz_server.dto.rating.RatingIndicatorSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 플랜의 상세한 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *     <li> 플랜(Plan)은 플래너(Planner)의 특정 날짜에 추가된 레시피입니다. </li>
 *     <li> Plan 및 Recipe의 대부분의 기본 정보와 Author, Rating, Liked, SavedRecipe 등의
 *          연관 관계 엔티티 정보, 로그인한 사용자의 context도 포함합니다. </li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanDetailDto {

    private Long id;

    /**
     * 플랜을 등록한 사용자의 ID
     */
    private Long userId;

    /**
     * 레시피가 플래너에 저장된 날짜 (플랜 날짜)
     */
    private LocalDateTime scheduledAt;

    /**
     * 레시피의 ID
     */
    private Long recipeId;

    /**
     * 레시피 작성자의 ID
     */
    private Long recipeAuthorId;

    /**
     * 레시피 작성자의 사용자 이름
     */
    private String recipeAuthorUsername;

    /**
     * 레시피의 제목
     */
    private String recipeTitle;

    /**
     * 레시피의 대표 이미지 URL 주소
     */
    private String recipeImageUrl;

    /**
     * 레시피의 요리 시간
     * 시간 단위로 '초'를 사용합니다.
     */
    private Integer recipeCookingTime;

    /**
     * 레시피의 준비 시간
     * 시간 단위로 '초'를 사용합니다.
     */
    private Integer recipePrepTime;

    /**
     * 레시피에 등록된 모든 평가에 대한 평가 요약 지표 정보
     */
    private RatingIndicatorSummaryDto ratingIndicatorSummary;

    /**
     * 레시피를 좋아하는 사람 여부
     */
    private boolean isLikedRecipeByUser;

    /**
     * 레시피를 저장한 사람 여부
     */
    private boolean isSavedRecipeByUser;

    /**
     * 레시피의 작성자 여부
     */
    private boolean isOwnedRecipeByUser;

    public PlanDetailDto(
            Long id,
            Long userId,
            LocalDateTime scheduledAt,
            Long recipeId,
            Long recipeAuthorId,
            String recipeAuthorUsername,
            String recipeTitle,
            String recipeImageUrl,
            Integer recipeCookingTime,
            Integer recipePrepTime,
            long ratingCount,
            double ratingAverageScore,
            boolean isOwnedRecipeByUser,
            boolean isLikedRecipeByUser,
            boolean isSavedRecipeByUser) {
        this.id = id;
        this.userId = userId;
        this.scheduledAt = scheduledAt;
        this.recipeId = recipeId;
        this.recipeAuthorId = recipeAuthorId;
        this.recipeAuthorUsername = recipeAuthorUsername;
        this.recipeTitle = recipeTitle;
        this.recipeImageUrl = recipeImageUrl;
        this.recipeCookingTime = recipeCookingTime;
        this.recipePrepTime = recipePrepTime;
        this.ratingIndicatorSummary = new RatingIndicatorSummaryDto(ratingAverageScore, ratingCount);
        this.isOwnedRecipeByUser = isOwnedRecipeByUser;
        this.isLikedRecipeByUser = isLikedRecipeByUser;
        this.isSavedRecipeByUser = isSavedRecipeByUser;
    }

}
