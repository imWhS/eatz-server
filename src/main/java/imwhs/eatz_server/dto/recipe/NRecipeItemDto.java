package imwhs.eatz_server.dto.recipe;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.dto.eatzuser.NEatzUserEssentialsDto;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;

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
public class NRecipeItemDto {

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
     * 레시피를 등록한 사용자의 핵심 정보. (with EatzUser)
     */
    private NEatzUserEssentialsDto user;

    /**
     * 재료 목록. (from IngredientRecipe)
     */
    private List<IngredientDto> ingredients;

    /**
     * 레시피의 카테고리 목록. (from RecipeCategory)
     */
    private List<CategoryDto> categories;

    /**
     * 레시피의 댓글 수. (from Comment)
     */
    private Long commentCount;

    /**
     * 레시피를 좋아하는 사용자 수. (from Likes)
     */
    private Long likeCount;

    /**
     * 좋아요 여부. (from Likes)
     */
    private boolean isLikedByUser;

    /**
     * 레시피를 저장한 사용자 수. (from SavedRecipe)
     */
    private Long savedCount;

    /**
     * 저장 여부. (from SavedRecipe)
     */
    private boolean savedByUser;

    /**
     * 레시피에 등록된 평가 요약 정보. (from Rating)
     */
    private RatingSummaryDto rating;

    public NRecipeItemDto(
            Long id,
            String title,
            String imageUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            NEatzUserEssentialsDto user) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imageUrl = imageUrl;
        this.user = user;
    }

}
