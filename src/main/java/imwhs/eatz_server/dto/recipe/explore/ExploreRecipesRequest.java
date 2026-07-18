package imwhs.eatz_server.dto.recipe.explore;

import imwhs.eatz_server.domain.recipe.ExploreRecipesSort;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExploreRecipesRequest {

    /**
     * 레시피의 검색어 (키워드).
     * 레시피 제목, 설명에 포함되어야 할 내용입니다.
     */
    private String keyword;

    /**
     * 레시피의 최대 소요 시간.
     * 소요 시간은 요리 시간과 준비 시간의 합입니다.
     */
    private Integer maxTotalTime;

    /**
     * 레시피의 1회 제공량
     */
    private Integer servings;

    /**
     * 레시피에 추가되어 있는 태그(테마)의 ID
     */
    private Long tagId;

    /**
     * 정렬
     */
    private ExploreRecipesSort sort;

}
