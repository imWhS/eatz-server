package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class KitchenwareNotFoundException extends BaseException {

    public KitchenwareNotFoundException() {
        super(EatzCommonErrorType.KITCHENWARE_NOT_FOUND);
    }

    public KitchenwareNotFoundException(Long id) {
        super(EatzCommonErrorType.KITCHENWARE_NOT_FOUND, "ID가 " + id + "인 도구를 찾을 수 없어요.");
    }

    public KitchenwareNotFoundException(String name) {
        super(EatzCommonErrorType.KITCHENWARE_NOT_FOUND, "이름이 " + name + "인 도구를 찾을 수 없어요.");
    }

}
