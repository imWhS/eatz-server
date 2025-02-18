package imwhs.eatz_server.exception;

public class PlanNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE_INVALID = "플랜을 찾을 수 없어요.";

    private static final String MESSAGE_TEMPLATE_INVALID_ID = "ID가 %d인 플랜을 찾을 수 없어요.";

    public PlanNotFoundException() {
        super();
    }

    public PlanNotFoundException(Long id) {
        super(String.format(MESSAGE_TEMPLATE_INVALID_ID, id));
    }

    public PlanNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlanNotFoundException(Throwable cause) {
        super(cause);
    }

}
