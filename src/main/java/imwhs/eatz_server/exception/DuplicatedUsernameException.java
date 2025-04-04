package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.ErrorCodeUser;

public class DuplicatedUsernameException extends BaseException {

    public DuplicatedUsernameException() {
        super(ErrorCodeUser.DUPLICATED_USER_USERNAME);
    }

}
