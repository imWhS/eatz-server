package imwhs.eatz_server.domain.converter;

import imwhs.eatz_server.domain.recipe.CookableRecipesSort;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCookableRecipesSortConverter implements Converter<String, CookableRecipesSort> {

    @Override
    public @Nullable CookableRecipesSort convert(String source) {
        if (source.isBlank()) {
            return null;
        }

        for (CookableRecipesSort sort : CookableRecipesSort.values()) {
            if (sort.getCode().equalsIgnoreCase(source.trim())) {
                return sort;
            }
        }

        return null;
    }

}
