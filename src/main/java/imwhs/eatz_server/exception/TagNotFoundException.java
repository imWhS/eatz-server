package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class TagNotFoundException extends BaseException {

    public TagNotFoundException() {
        super(EatzCommonErrorType.TAG_NOT_FOUND);
    }

    public TagNotFoundException(Long id) {
        super(EatzCommonErrorType.TAG_NOT_FOUND, "ID가 " + id + "인 태그를 찾을 수 없어요.");
    }

}