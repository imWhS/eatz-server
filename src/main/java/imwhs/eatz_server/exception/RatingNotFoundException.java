package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzRatingErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class RatingNotFoundException extends BaseException {

    public RatingNotFoundException() { super(EatzRatingErrorType.NOT_FOUND); }

    public RatingNotFoundException(Long id) {
        super(EatzRatingErrorType.NOT_FOUND, "ID가 " + id + "인 평가를 찾을 수 없어요.");
    }

}
