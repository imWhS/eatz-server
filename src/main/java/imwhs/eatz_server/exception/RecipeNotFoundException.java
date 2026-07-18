package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzRecipeErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class RecipeNotFoundException extends BaseException {

    public RecipeNotFoundException() {
        super(EatzRecipeErrorType.NOT_FOUND);
    }

    public RecipeNotFoundException(Long id) {
        super(EatzRecipeErrorType.NOT_FOUND, "ID가 " + id + "인 레시피를 찾을 수 없어요.");
    }

}
