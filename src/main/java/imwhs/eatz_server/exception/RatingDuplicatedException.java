package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzRatingErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class RatingDuplicatedException extends BaseException {

    public RatingDuplicatedException() {
        super(EatzRatingErrorType.DUPLICATED);
    }

}
