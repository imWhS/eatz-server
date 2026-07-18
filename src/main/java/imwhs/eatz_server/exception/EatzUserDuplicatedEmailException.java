package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzUserErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class EatzUserDuplicatedEmailException extends BaseException {

    public EatzUserDuplicatedEmailException() {
        super(EatzUserErrorType.DUPLICATED_EMAIL);
    }

}
