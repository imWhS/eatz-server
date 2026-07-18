package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzAuthVerificationErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class VerificationCodeReissuableIntervalRemainingException extends BaseException {

    private static final String MESSAGE_TEMPLATE =
            "오늘 이미 입력하신 이메일 주소로 가입하기 위한 인증 코드를 발급해드린 적이 있어요. " +
            "%s초 후에 가입을 위한 인증 코드를 다시 발급할 수 있어요.";

    public VerificationCodeReissuableIntervalRemainingException(long reissuableSeconds) {
        super(
                EatzAuthVerificationErrorType.CODE_REISSUABLE_INTERVAL_REMAINING,
                String.format(MESSAGE_TEMPLATE, reissuableSeconds));
    }

}