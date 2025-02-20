package imwhs.eatz_server.exception;

public class EatzUserNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE_INVALID = "사용자를 찾을 수 없어요.";

    private static final String MESSAGE_TEMPLATE_INVALID_USERNAME = "사용자 이름이 %s인 사용자를 찾을 수 없어요.";

    private static final String MESSAGE_TEMPLATE_INVALID_EMAIL = "사용자 이름이 %s인 사용자를 찾을 수 없어요.";

    private static final String MESSAGE_TEMPLATE_INVALID_ID = "ID가 %s인 사용자를 찾을 수 없어요.";

    public EatzUserNotFoundException() {
        super();
    }

    public EatzUserNotFoundException(String username) {
        super(String.format(MESSAGE_TEMPLATE_INVALID_USERNAME, username));
    }

    public EatzUserNotFoundException(String email, boolean isEmail) {
        super(String.format(MESSAGE_TEMPLATE_INVALID_EMAIL, email));
    }

    public EatzUserNotFoundException(Long id) {
        super(String.format(MESSAGE_TEMPLATE_INVALID_ID, id));
    }

    public EatzUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EatzUserNotFoundException(Throwable cause) {
        super(cause);
    }

}
