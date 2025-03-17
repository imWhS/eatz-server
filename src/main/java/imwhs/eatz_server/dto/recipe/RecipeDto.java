package imwhs.eatz_server.dto.recipe;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import imwhs.eatz_server.dto.recipe.category.CategoryBasicDto;
import imwhs.eatz_server.dto.recipe.ingredient.RecipeIngredientDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RecipeDto 클래스입니다.
 * <p>
 *     레시피의 상세 정보를 전달하기 위한 DTO로 사용합니다.
 * </p>
 */
@Data
public class RecipeDto {

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
     * 레시피 조회 수
     */
    private Integer viewCount;

    // 아래부터는 xToOne 연관 관계 엔티티 관련 DTO 타입의 필드입니다.

    /**
     * 레시피를 등록한 사용자 정보. (with EatzUser)
     */
    private AuthorOfRecipeDto author;

    /**
     * 재료 목록. (from IngredientRecipe)
     */
    private List<RecipeIngredientDto> ingredients = new ArrayList<>();

    /**
     * 레시피의 카테고리 목록. (from RecipeCategory)
     */
    private List<CategoryBasicDto> categories = new ArrayList<>();

    /**
     * 레시피의 댓글 수. (from Comment)
     */
    private Long commentCount;

    /**
     * 레시피를 좋아하는 사용자 수. (from Liked)
     */
    private Long likedCount;

    /**
     * 레시피에 등록된 평가 요약 정보. (from Rating)
     */
    private RatingSummaryDto rating;

    /*
    TODO: 사용자들의 레시피 저장 수. (from SavedRecipe)
     */
//    private Long savedCount;


    public RecipeDto(Long id, String title, String description, String imageUrl, Long cookingTime, Long prepTime, LocalDateTime createdAt, LocalDateTime updatedAt, Integer viewCount, AuthorOfRecipeDto author) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.cookingTime = toMinutes(cookingTime);
        this.prepTime = toMinutes(prepTime);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.viewCount = viewCount;
        this.author = author;
    }

    public RecipeDto(Long id, String title, String description, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt, Integer viewCount, AuthorOfRecipeDto author) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.viewCount = viewCount;
        this.author = author;
    }

    private Integer toMinutes(Long seconds) {
        return (seconds == null) ? null : (int) (seconds / 60);
    }

    @Data
    @AllArgsConstructor
    public static class AuthorOfRecipeDto {

        private Long id;

        private String username;

        private String imageUrl;

        private Long recipeCount;

    }

}
