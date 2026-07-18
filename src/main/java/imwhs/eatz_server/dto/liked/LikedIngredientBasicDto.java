package imwhs.eatz_server.dto.liked;

import imwhs.eatz_server.domain.liked.LikedIngredient;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 좋아하는 재료(LikedIngredient)의 기본 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> LikedIngredient의 핵심 및 대부분의 정보를 포함합니다. </li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class LikedIngredientBasicDto {

    private Long id;
    private Long ingredientId;
    private boolean isLiked;
    private long count;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LikedIngredientBasicDto(LikedIngredient likedIngredient, long count) {
        // 한 번도 좋아요 한 적 없던 사용자가 unlike를 한 경우에는
        // 불필요하게 LikedIngredient를 생성하지 않기 때문에, NPE를 방지하기 위해 분기합니다.
        if (likedIngredient == null) {
            this.isLiked = false;
        } else {
            this.id = likedIngredient.getId();
            this.ingredientId = likedIngredient.getIngredient().getId();
            this.isLiked = likedIngredient.isLiked();
            this.createdAt = likedIngredient.getCreatedAt();
            this.updatedAt = likedIngredient.getUpdatedAt();
        }

        this.count = count;
    }

}
