package imwhs.eatz_server.exception;

public class IngredientNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE_INVALID_NAME = "이름이 %s인 재료를 찾을 수 없어요.";

    private static final String MESSAGE_TEMPLATE_INVALID_ID = "ID가 %s인 재료를 찾을 수 없어요.";

    public IngredientNotFoundException() {
        super();
    }

    public IngredientNotFoundException(String name) {
        super(String.format(MESSAGE_TEMPLATE_INVALID_NAME, name));
    }

    public IngredientNotFoundException(Long id) {
        super(String.format(MESSAGE_TEMPLATE_INVALID_ID, id));
    }

    public IngredientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public IngredientNotFoundException(Throwable cause) {
        super(cause);
    }

}
