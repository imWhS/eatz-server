package imwhs.eatz_server.domain.converter;

import imwhs.eatz_server.domain.recipe.ExploreRecipesSort;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToExploreRecipesSortConverter implements Converter<String, ExploreRecipesSort> {

    @Override
    public @Nullable ExploreRecipesSort convert(String source) {
        if (source.isBlank()) {
            return null;
        }

        for (ExploreRecipesSort sort : ExploreRecipesSort.values()) {
            if (sort.getCode().equalsIgnoreCase(source.trim())) {
                return sort;
            }
        }

        return null;
    }

}