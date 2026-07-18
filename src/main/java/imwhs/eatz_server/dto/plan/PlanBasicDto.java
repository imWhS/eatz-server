package imwhs.eatz_server.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 픞랜(Plan) 및 플랜으로 추가한 레시피의 기본 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> Plan의 핵심 및 대부분의 정보와 Recipe 등의 연관 관계 엔티티의 정보 및 현재 로그인 사용자의 context를 포함합니다. </li>
 *      <li> 주로 컬렉션으로 조회할 때 사용합니다. </li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class PlanBasicDto {

    private Long id;

    private LocalDateTime scheduledAt;

    private Long recipeId;

    private String recipeTitle;

    private String recipeImageUrl;

    /**
     * 요리 시간.
     * 시간 단위로 '초'를 사용합니다.
     */
    private Integer recipeCookingTime;

    /**
     * 준비 시간.
     * 시간 단위로 '초'를 사용합니다.
     */
    private Integer recipePrepTime;

    private Long recipeAuthorId;

    private String recipeAuthorUsername;

    private boolean isOwnedRecipeByUser;

    private boolean isLikedRecipeByUser;

    private boolean isSavedRecipeByUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlanBasicDto that = (PlanBasicDto) o;
        return Objects.equals(recipeId, that.recipeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId);
    }

}
