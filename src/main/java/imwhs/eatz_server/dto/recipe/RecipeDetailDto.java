package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.dto.rating.RatingIndicatorSummaryDto;
import imwhs.eatz_server.dto.tag.TagEssentialDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 레시피(Recipe)의 상세한 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> Recipe의 대부분의 기본 정보와 Author, Rating, Comment 등의 연관 관계 엔티티 정보, 로그인한 사용자의 context도 포함합니다. </li>
 *      <li> 단, RecipeTag와 같이 1:N 연관 관계를 설정하는 컬렉션 필드는,
 *           성능 최적화(카테시안 곱 방지)를 위해 생성자가 아닌 setter를 통해 별도로 초기화해야 합니다. </li>
 * </ul>
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class RecipeDetailDto {

    /**
     * 레시피의 ID
     */
    private Long id;

    /**
     * 레시피의 제목
     */
    private String title;

    /**
     * 레시피의 설명
     */
    private String description;

    /**
     * 레시피의 대표 이미지 URL
     */
    private String imageUrl;

//    /**
//     * 레시피의 URL
//     */
//    private String url;

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
     * 레시피의 창작자 이름
     */
    private String creatorName;

    /**
     * 레시피의 창작자 관련 URL
     */
    private String creatorUrl;

    /**
     * 레시피의 1회 제공량
     */
    private Integer servings;

    /**
     * 레시피의 등록 날짜
     */
    private LocalDateTime createdAt;

    /**
     * 레시피의 업데이트 날짜
     */
    private LocalDateTime updatedAt;

    /**
     * 레시피의 댓글 활성화 여부
     */
    private boolean isCommentEnabled;

    /**
     * 레시피의 조회 수
     */
    private long viewCount;

    // 아래부터는 xToOne 연관 관계 엔티티 관련 DTO 타입의 필드입니다.

    /**
     * 레시피의 작성자 정보 (with EatzUser)
     */
    private RecipeDetailAuthorDto author;

    /**
     * 레시피의 댓글 수 (from Comment)
     */
    private long commentCount;

    /**
     * 레시피를 좋아하는 사용자 수 (from Liked)
     */
    private long likedCount;

    /**
     * 사용자의 좋아하는 사람 여부
     */
    private boolean isLiked;

    /**
     * 사용자의 저장한 레시피 여부
     */
    private boolean isSaved;

    /**
     * 레시피에 달린 모든 평가의 요약 정보 (from Rating)
     */
    private RatingIndicatorSummaryDto ratingIndicatorSummary;

    /*
    TODO: 사용자들의 레시피 저장 수 (from SavedRecipe)
     */
//    private Long savedCount;

    /**
     * 레시피가 속한 모든 태그 목록 (from RecipeTag)
     */
    private List<TagEssentialDto> tags = new ArrayList<>();

    /**
     * QueryDSL의 메인 쿼리 프로젝션을 위한 생성자입니다.
     * <p>
     *     레시피가 속한 태그(tags) 등 1:N 연관 관계의 컬렉션 필드는 카테시안 곱을 방지하고,
     *     쿼리 성능을 최적화하기 위해 메인 쿼리 프로젝션 대상에서 제외합니다.
     * </p>
     */
    public RecipeDetailDto(
            Long id,
            String title,
            String description,
            String imageUrl,
//            String url,
            Integer cookingTime,
            Integer prepTime,
            String creatorName,
            String creatorUrl,
            Integer servings,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            boolean isCommentEnabled,
            long viewCount,
            RecipeDetailAuthorDto author,
            long commentCount,
            long likedCount,
            boolean isLiked,
            boolean isSaved,
            RatingIndicatorSummaryDto ratingsEssential
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
//        this.url = url;
        this.cookingTime = cookingTime;
        this.prepTime = prepTime;
        this.creatorName = creatorName;
        this.creatorUrl = creatorUrl;
        this.servings = servings;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isCommentEnabled = isCommentEnabled;
        this.viewCount = viewCount;
        this.author = author;
        this.commentCount = commentCount;
        this.likedCount = likedCount;
        this.isLiked = isLiked;
        this.isSaved = isSaved;
        this.ratingIndicatorSummary = ratingsEssential;
    }

    public RecipeDetailDto(
            Long id,
            String title,
            String description,
            String imageUrl,
//            String url,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            boolean isCommentEnabled,
            long viewCount,
            RecipeDetailAuthorDto author) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
//        this.url = url;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isCommentEnabled = isCommentEnabled;
        this.viewCount = viewCount;
        this.author = author;
    }

    @EqualsAndHashCode
    @Getter
    @AllArgsConstructor
    public static class RecipeDetailAuthorDto {

        private Long id;
        private String username;
        private String imageUrl;
        private String bio;
        private long recipeCount;

    }

}
