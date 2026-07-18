package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class EmailNotVerifiedException extends BaseException {

    public EmailNotVerifiedException() {
        super(EatzAuthErrorType.NOT_VERIFIED_SIGN_IN_EMAIL);
    }

}