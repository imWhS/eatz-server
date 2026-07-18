package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthVerificationErrorType;
import imwhs.eatz_server.exception.base.BaseException;
import lombok.Getter;

@Getter
public class VerificationCodeValidationMismatchException extends BaseException {

    public VerificationCodeValidationMismatchException() {
        super(EatzAuthVerificationErrorType.CODE_VALIDATION_MISMATCH);
    }

}
