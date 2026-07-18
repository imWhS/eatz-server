package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class LikedNotFoundException extends BaseException {

    public LikedNotFoundException() {
        super(EatzCommonErrorType.LIKED_NOT_FOUND);
    }

    public LikedNotFoundException(Long id) {
        super(EatzCommonErrorType.LIKED_NOT_FOUND, "ID가 " + id + "인 좋아요를 찾을 수 없어요.");
    }

}
