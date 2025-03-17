package imwhs.eatz_server.dto.recipe;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.recipe.category.CategoryBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * RecipeItemDto 클래스입니다.
 * <p>
 *     레시피 및 레시피와 연관된 엔티티의 요약 정보를 함께 전달하기 위한 DTO입니다.
 *     여러 개의 레시피가 하나의 목록에 나열되어질 때 주로 사용합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class RecipeItemDto {

    /**
     * 레시피 ID.
     */
    private Long id;

    /**
     * 레시피 제목.
     */
    private String title;

    /**
     * 레시피 대표 이미지 URL 주소.
     */
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
     * 레시피 등록 날짜.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 레시피 업데이트 날짜.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 조회 수.
     */
    private Integer viewCount;

    /**
     * 레시피를 등록한 사용자의 핵심 정보. (with EatzUser)
     */
    private EatzUserBasicDto author;

    /**
     * 좋아요 여부. (from Likes)
     */
    private boolean isLikedByUser;

    /**
     * 레시피의 댓글 수. (from Comment)
     */
    private Long commentCount;

    /**
     * 레시피를 좋아하는 사용자 수. (from Liked)
     */
    private Long likedCount;

    /**
     * 저장 여부. (from SavedRecipe)
     */
    private boolean savedByUser;

    /**
     * 레시피를 저장한 사용자 수. (from SavedRecipe)
     */
    private Long savedCount;

    /**
     * 재료 목록. (from IngredientRecipe)
     */
    private List<IngredientDto> ingredients;

    /**
     * 레시피의 카테고리 목록. (from RecipeCategory)
     */
    private List<CategoryBasicDto> categories;

    /**
     *  레시피에 달린 평가 수
     */
    private Long ratingCount;

    /**
     * 레시피에 달린 평가들의 평균 점수.
     * 1~5점 사이의 자연수 값이며, 사용자들이 남긴 평가 점수의 평균입니다.
     */
    private Double averageRatingScore;

    public RecipeItemDto(
            Long id,
            String title,
            String imageUrl,
            Long cookingTime,
            Long prepTime,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Integer viewCount,
            EatzUserBasicDto author,
            Boolean isLikedByUser,
            Long commentCount,
            Long likedCount,
            Long savedCount,
            Long ratingCount,
            Double averageRatingScore) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.viewCount = viewCount;
        this.imageUrl = imageUrl;
        this.cookingTime = toMinutes(cookingTime);
        this.prepTime = toMinutes(prepTime);
        this.author = author;
        this.isLikedByUser = isLikedByUser;
        this.commentCount = commentCount;
        this.likedCount = likedCount;
        this.savedCount = savedCount;
        this.ratingCount = ratingCount;
        this.averageRatingScore = averageRatingScore;
    }

    private Integer toMinutes(Long seconds) {
        return (seconds == null) ? null : (int) (seconds / 60);
    }

}
