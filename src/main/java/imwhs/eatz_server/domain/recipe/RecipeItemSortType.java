package imwhs.eatz_server.domain.recipe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RecipeItemSortType {

    LATEST("latest"),
    HIGHEST_RATED("highest_rated"),
    MOST_LIKED("mostLiked");

    private final String value;

    public static RecipeItemSortType of(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(LATEST);
    }

}
