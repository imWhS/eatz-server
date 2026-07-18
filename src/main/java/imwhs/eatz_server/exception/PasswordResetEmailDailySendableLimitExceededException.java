package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthPasswordResetErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class PasswordResetEmailDailySendableLimitExceededException extends BaseException {

    public PasswordResetEmailDailySendableLimitExceededException() {
        super(EatzAuthPasswordResetErrorType.EMAIL_REACHED_DAILY_SENDABLE_LIMIT);
    }

}