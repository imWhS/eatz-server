package imwhs.eatz_server.converter;

import imwhs.eatz_server.domain.recipe.CookableRecipesSort;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 컨트롤러가 클라이언트의 HTTP 요청으로 전달 받은 쿼리 파라미터 문자열을 CookableRecipesSort로 변환합니다.
 */
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

        throw new EatzInvalidRequestArgumentException("올바르지 않은 요청 파라미터 값이에요: " + source);
    }

}
