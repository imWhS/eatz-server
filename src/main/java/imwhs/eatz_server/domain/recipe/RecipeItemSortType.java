package imwhs.eatz_server.domain.recipe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RecipeItemSortType {

    LATEST,
    HIGHEST_RATED,
    MOST_LIKED

}
