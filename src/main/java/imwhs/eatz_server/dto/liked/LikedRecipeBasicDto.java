package imwhs.eatz_server.dto.liked;

import imwhs.eatz_server.domain.liked.LikedRecipe;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 좋아하는 레시피(LikedRecipe)의 기본 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> LikedRecipe의 핵심 및 대부분의 정보를 포함합니다. </li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class LikedRecipeBasicDto {

    private Long id;
    private Long recipeId;
    private boolean isLiked;
    private long count;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LikedRecipeBasicDto(LikedRecipe likedRecipe, long count) {
        // 한 번도 좋아요 한 적 없던 사용자가 unlike를 한 경우에는
        // 불필요하게 LikedRecipe를 생성하지 않기 때문에, NPE를 방지하기 위해 분기합니다.
        if (likedRecipe == null) {
            this.isLiked = false;
        } else {
            this.id = likedRecipe.getId();
            this.recipeId = likedRecipe.getRecipe().getId();
            this.isLiked = likedRecipe.isLiked();
            this.createdAt = likedRecipe.getCreatedAt();
            this.updatedAt = likedRecipe.getUpdatedAt();
        }

        this.count = count;
    }

}
