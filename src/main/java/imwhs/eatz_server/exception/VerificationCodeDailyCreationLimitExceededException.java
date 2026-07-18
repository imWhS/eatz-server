package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthVerificationErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class VerificationCodeDailyCreationLimitExceededException extends BaseException {

    public VerificationCodeDailyCreationLimitExceededException() {
        super(EatzAuthVerificationErrorType.CODE_DAILY_CREATION_LIMIT_EXCEEDED);
    }

}