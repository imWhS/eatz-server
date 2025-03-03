package imwhs.eatz_server.domain.recipe;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecipeItemSortType {

    LATEST("latest"),
    HIGHEST_RATED("highestRated"),
    MOST_LIKED("mostLiked"),;

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

}
