package imwhs.eatz_server.dto.recipe.cookable;

import imwhs.eatz_server.domain.recipe.CookableRecipesSort;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * '지금 요리(Cookable)' 레시피 정보 목록을 요청하기 위해 필요한 정보를 담은 DTO입니다.
 */
@Data
@AllArgsConstructor
public class CookableRecipesRequest {

    /**
     * 지금 요리 가능한 레시피만 필터링 여부. 기본 값은 true 입니다.
     * true로 설정하면, 사용자가 보관함에 추가한 재료, 도구만으로 요리 가능한 레시피만 포함합니다.
     */
    @NotNull
    private Boolean isCookableOnly;

    /**
     * 검색어 (키워드)
     * 레시피 제목, 설명에 포함되어야 할 내용입니다.
     */
    private String keyword;

    /**
     * 레시피 최대 소요 시간
     * 시간 단위로 '초'를 사용합니다.
     */
    private Integer maxTotalTime;

    /**
     * 레시피 1회 제공량
     */
    private Integer servings;

    /**
     * 레시피 정렬
     */
    private CookableRecipesSort sort;

    public Boolean getCookableOnly() {
        return isCookableOnly == null || isCookableOnly;
    }

}
