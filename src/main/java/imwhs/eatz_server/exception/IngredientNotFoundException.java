package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class IngredientNotFoundException extends BaseException {

    public IngredientNotFoundException() {
        super(EatzCommonErrorType.INGREDIENT_NOT_FOUND);
    }

    public IngredientNotFoundException(Long id) {
        super(EatzCommonErrorType.INGREDIENT_NOT_FOUND, "ID가 " + id + "인 재료를 찾을 수 없어요.");
    }

    public IngredientNotFoundException(String name) {
        super(EatzCommonErrorType.INGREDIENT_NOT_FOUND, "이름이 " + name + "인 재료를 찾을 수 없어요.");
    }

}
