package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class ReportReasonIsNotActiveException extends BaseException {

    private static final String MESSAGE_TEMPLATE = "코드 %s는 비활성화된 신고 이유예요.";

    public ReportReasonIsNotActiveException() {
        super(EatzCommonErrorType.REPORT_REASON_IS_NOT_ACTIVE);
    }

    public ReportReasonIsNotActiveException(String code) {
        super(EatzCommonErrorType.REPORT_REASON_IS_NOT_ACTIVE, String.format(MESSAGE_TEMPLATE, code));
    }

}
