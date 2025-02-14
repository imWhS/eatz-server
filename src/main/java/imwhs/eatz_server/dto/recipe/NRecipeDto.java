package imwhs.eatz_server.dto.recipe;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.dto.eatzuser.EatzUserWithRecipeSummaryDto;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.ingredient.IngredientWithCategoryChildDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RecipeDto 클래스입니다.
 * <p>
 *     레시피 정보를 전달하기 위한 DTO로 사용합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class NRecipeDto {

    /**
     * 레시피 ID.
     */
    private Long id;

    /**
     * 레시피 제목.
     */
    private String title;

    /**
     * 레시피 설명.
     */
    private String description;

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

    // 아래부터는 xToOne 연관 관계 엔티티 관련 DTO 타입의 필드입니다.

    /**
     * 레시피를 등록한 사용자 정보.
     */
    private EatzUserWithRecipeSummaryDto user;

    // 아래부터는 xToMany 연관 관계 엔티티 관련 DTO 타입의 필드입니다.

    /**
     * 레시피에 등록된 모든 댓글 수. (from Comment)
     */
    private Long commentCount;

    /**
     * 레시피를 좋아하는 사용자 수. (from Likes)
     */
    private Long likeCount;

    /**
     * 레시피가 등록되어져 있는 모든 카테고리 목록. (from RecipeCategory)
     */
    private List<CategoryDto> categories;

    /**
     * 레시피에 등록된 평가 요약 정보. (from Rating)
     */
    private RatingSummaryDto rating;

    /*
    TODO: 재료 목록.
     */
    private List<IngredientDto> ingredients;

    /*
    TODO: 사용자들의 레시피 저장 수. (from SavedRecipe)
     */
//    private Long savedCount;


    public NRecipeDto(
            Long id,
            String title,
            String description,
            String imageUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            EatzUserWithRecipeSummaryDto user) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imageUrl = imageUrl;
        this.user = user;
    }

}
