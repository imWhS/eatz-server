package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.ingredient.IngredientEssentialDto;
import imwhs.eatz_server.dto.kitchenware.KitchenwareEssentialDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 레시피를 업데이트하기 위해 필요한 정보(draft)를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> Recipe의 대부분의 필드 뿐 아니라, IngredientRecipe, KitchenwareRecipe와 같은 1:N 연관 관계를 설정하는
 *           컬렉션 필드를 포함합니다. </li>
 *      <li> 단, IngredientRecipe, KitchenwareRecipe와 같이 1:N 연관 관계를 설정하는 컬렉션 필드는,
 *           성능 최적화(카테시안 곱 방지)를 위해 생성자가 아닌 setter를 통해 별도로 초기화해야 합니다. </li>
 * </ul>
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class RecipeEditableDto {

    private Long id;

    private String title;

    private String url;

    private String imageUrl;

    private String description;

    /**
     * 요리 시간. '분' 단위를 사용합니다.
     */
    private Integer cookingTime;

    /**
     * 준비 시간. '분' 단위를 사용합니다.
     */
    private Integer prepTime;

    /**
     * 1회 제공량
     */
    private Integer servings;

    @Setter
    private List<IngredientEssentialDto> ingredients = new ArrayList<>();

    @Setter
    private List<KitchenwareEssentialDto> kitchenwares = new ArrayList<>();

    private List<String> tagNames = new ArrayList<>();

    private Boolean isCommentEnabled;

    private String creatorName;

    private String creatorUrl;

    private RecipeEditableDto(
            Long id,
            String title,
            String url,
            String imageUrl,
            String description,
            Integer cookingTime,
            Integer prepTime,
            Integer servings,
            Boolean isCommentEnabled,
            String creatorName,
            String creatorUrl) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.description = description;
        this.cookingTime = cookingTime;
        this.prepTime = prepTime;
        this.servings = servings;
        this.isCommentEnabled = isCommentEnabled;
        this.creatorName = creatorName;
        this.creatorUrl = creatorUrl;
    }

    public static RecipeEditableDto from(Recipe recipe) {
        return new RecipeEditableDto(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getUrl(),
                recipe.getImageUrl(),
                recipe.getDescription(),
                recipe.getCookingTime(),
                recipe.getPrepTime(),
                recipe.getServings(),
                recipe.getIsCommentEnabled(),
                recipe.getCreatorName(),
                recipe.getCreatorUrl()
        );
    }
}
