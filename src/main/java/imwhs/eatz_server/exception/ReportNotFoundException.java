package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class ReportNotFoundException extends BaseException {

    private static final String MESSAGE_TEMPLATE_INVALID_REPORT_ID = "ID가 %s인 신고를 찾을 수 없어요.";

    public ReportNotFoundException() {
        super(EatzCommonErrorType.REPORT_NOT_FOUND);
    }

    public ReportNotFoundException(Long id) {
        super(EatzCommonErrorType.REPORT_NOT_FOUND, String.format(MESSAGE_TEMPLATE_INVALID_REPORT_ID, id));
    }

}
