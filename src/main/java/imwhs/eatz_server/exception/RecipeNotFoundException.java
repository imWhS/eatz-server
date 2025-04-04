package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.ErrorCode;

public class RecipeNotFoundException extends BaseException {

    public RecipeNotFoundException() {
        super(ErrorCode.RECIPE_NOT_FOUND);
    }

    public RecipeNotFoundException(Long id) {
        super(ErrorCode.RECIPE_NOT_FOUND, "ID가 " + id + "인 레시피를 찾을 수 없어요.");
    }

}
