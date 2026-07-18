package imwhs.eatz_server.domain.recipe;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CookableRecipesSort implements RecipeSort {

    FEWEST_MISSING_REQUIREMENTS("fewest_missing_items"),
    LATEST("latest"),
    HIGHEST_RATED("highestRated"),
    MOST_LIKED("mostLiked"),
    TRENDING("trending");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

}
