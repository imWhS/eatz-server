package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class ThemeNoNameNotFoundException extends BaseException {

    public ThemeNoNameNotFoundException() {
        super(EatzCommonErrorType.THEME_NOT_FOUND, "이름 없는 테마를 찾을 수 없어요.");
    }

}
