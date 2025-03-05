package imwhs.eatz_server.exception;

public class ReportNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE_INVALID_REPORT_ID = "ID가 %s인 신고를 찾을 수 없어요.";


    public ReportNotFoundException() {
        super();
    }

    public ReportNotFoundException(Long id) {
        super(String.format(MESSAGE_TEMPLATE_INVALID_REPORT_ID, id));
    }

    public ReportNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportNotFoundException(Throwable cause) {
        super(cause);
    }

}
