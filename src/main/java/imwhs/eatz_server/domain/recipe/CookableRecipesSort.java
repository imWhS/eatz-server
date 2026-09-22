package imwhs.eatz_server.domain.recipe;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CookableRecipesSort implements RecipeSort {

    FEWEST_MISSING_REQUIREMENTS("fewestMissingRequirements"),
    LATEST("latest"),
    HIGHEST_RATED("highestRated"),
    MOST_LIKED("mostLiked"),
    TRENDING("trending");

    /**
     * HTTP 통신 시 클라이언트와 규격을 맞추기 위해 사용할 문자열 값입니다.
     * StringToCookableRecipesSortConverter가 HTTP 요청 데이터를 역직렬화할 때 바인딩 기준으로서 사용하며,
     * HTTP 응답을 생성하기 위해 @JsonValue를 통해 직렬화할 때에도 사용됩니다.
     */
    private final String code;

    @JsonValue
    public String getCode() {
        return code;
    }

}
