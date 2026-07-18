package imwhs.eatz_server.dto.recipe.cookable;

import imwhs.eatz_server.dto.ingredient.IngredientEssentialDto;
import imwhs.eatz_server.dto.kitchenware.KitchenwareEssentialDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * '지금 요리(Cookable)'에서 사용할 레시피 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> Recipe의 기본 정보와 연관 관계 엔티티의 정보 및 현재 로그인 사용자의 context, 필요한 준비물 정보까지 포함합니다.</li>
 *      <li> 성능 최적화를 위해, Recipe와 1:N 연관 관계인
 *           IngredientRecipe, KitchenwareRecipe 관련 필드는 별도의 setter를 통해 설정합니다. </li>
 *      <li> 주로 컬렉션으로 조회할 때 사용합니다. </li>
 * </ul>
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class CookableRecipeDto {

    /**
     * 레시피의 ID
     */
    private Long id;

    /**
     * 레시피의 제목
     */
    private String title;

    /**
     * 레시피으 대표 이미지 URL
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
     * 레시피를 좋아하는 사람 수
     */
    private long likedCount;

    /**
     * 레시피에 달린 평가 수
     */
    private long ratingCount;

    /**
     * 레시피에 달린 댓글 수
     */
    private long commentCount;

    /**
     * 레시피에 달린 모든 평가들의 평균 점수
     * 1~5점 사이의 자연수 값이며, 사용자들이 남긴 평가 점수의 평균입니다.
     */
    private double ratingAverageScore;

    /**
     * 레시피를 좋아하는 사람 여부
     */
    private boolean isLikedByUser;

    /**
     * 레시피를 저장한 사용자 여부
     */
    private boolean isSavedByUser;

    /**
     * 사용자가 레시피를 요리하기 위해 추가해야 할 재료 수
     */
    private long missingIngredientCount;

    /**
     * 사용자가 레시피를 요리하기 위해 추가해야 할 도구 수
     */
    private long missingKitchenwareCount;

    /**
     * 사용자가 레시피를 요리하기 위해 추가해야 할 모든 재료의 핵심 정보 목록
     */
    @Setter
    private List<IngredientEssentialDto> missingIngredients = new ArrayList<>();

    /**
     * 사용자가 레시피를 요리하기 위해 추가해야 할 모든 도구의 핵심 정보 목록
     */
    @Setter
    private List<KitchenwareEssentialDto> missingKitchenwares = new ArrayList<>();

    /**
     * QueryDSL의 메인 쿼리 프로젝션을 위한 생성자입니다.
     * <p>
     *     레시피를 요리하기 위해 필요한 준비물에 해당하는 IngredientRecipe, KitchenwareRecipe 관련 필드는
     *     쿼리 성능을 최적화하기 위해 메인 쿼리 프로젝션 대상에서 제외합니다.
     * </p>
     */
    public CookableRecipeDto(
            Long id,
            String title,
            String imageUrl,
            Integer servings,
            Integer cookingTime,
            Integer prepTime,
            Long authorId,
            String authorUsername,
            long likedCount,
            long commentCount,
            long ratingCount,
            double ratingAverageScore,
            boolean isLikedByUser,
            boolean isSavedByUser,
            long missingIngredientCount,
            long missingKitchenwareCount
    ) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.servings = servings;
        this.cookingTime = cookingTime;
        this.prepTime = prepTime;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.likedCount = likedCount;
        this.commentCount = commentCount;
        this.ratingCount = ratingCount;
        this.ratingAverageScore = ratingAverageScore;
        this.isLikedByUser = isLikedByUser;
        this.isSavedByUser = isSavedByUser;
        this.missingIngredientCount = missingIngredientCount;
        this.missingKitchenwareCount = missingKitchenwareCount;
    }

}

