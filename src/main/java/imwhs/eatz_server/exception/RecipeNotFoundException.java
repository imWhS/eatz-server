package imwhs.eatz_server.exception;

public class RecipeNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE_INVALID = "레시피를 찾을 수 없어요.";

    private static final String MESSAGE_TEMPLATE_INVALID_ID = "ID가 %d인 레시피를 찾을 수 없어요.";

    public RecipeNotFoundException() {
        super();
    }

    public RecipeNotFoundException(Long id) {
        super(String.format(MESSAGE_TEMPLATE_INVALID_ID, id));
    }

    public RecipeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecipeNotFoundException(Throwable cause) {
        super(cause);
    }

}
