package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class ThemeNotFoundException extends BaseException {

    public ThemeNotFoundException() {
        super(EatzCommonErrorType.THEME_NOT_FOUND);
    }

    public ThemeNotFoundException(Long id) {
        super(EatzCommonErrorType.THEME_NOT_FOUND, "ID가 " + id + "인 테마를 찾을 수 없어요.");
    }

}
