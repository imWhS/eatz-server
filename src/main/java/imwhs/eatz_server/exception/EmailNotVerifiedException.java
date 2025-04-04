package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.ErrorCodeAuth;

public class EmailNotVerifiedException extends BaseException {

    public EmailNotVerifiedException() {
        super(ErrorCodeAuth.NOT_VERIFIED_SIGN_IN_EMAIL);
    }

}