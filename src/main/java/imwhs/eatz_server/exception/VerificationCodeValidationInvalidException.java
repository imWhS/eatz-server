package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthVerificationErrorType;
import imwhs.eatz_server.exception.base.BaseException;
import lombok.Getter;

@Getter
public class VerificationCodeValidationInvalidException extends BaseException {

    public VerificationCodeValidationInvalidException() {
        super(EatzAuthVerificationErrorType.CODE_VALIDATION_INVALID);
    }

}
