package imwhs.eatz_server.exception;

public class CategoryNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE_INVALID = "카테고리를 찾을 수 없어요.";

    private static final String MESSAGE_TEMPLATE_INVALID_ID = "ID가 %d인 카테고리를 찾을 수 없어요.";

    public CategoryNotFoundException() {
        super("카테고리가 존재하지 않아요.");
    }

    public CategoryNotFoundException(Long id) {
        super(String.format(MESSAGE_TEMPLATE_INVALID, id));
    }

    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryNotFoundException(Throwable cause) {
        super(cause);
    }

}
