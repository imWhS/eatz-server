package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.ErrorCodeUser;

public class DuplicatedEatzUserEmailException extends BaseException {

    public DuplicatedEatzUserEmailException() {
        super(ErrorCodeUser.DUPLICATED_USER_EMAIL);
    }


}
