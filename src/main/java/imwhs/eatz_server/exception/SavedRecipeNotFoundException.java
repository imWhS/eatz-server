package imwhs.eatz_server.exception;

public class SavedRecipeNotFoundException extends RuntimeException {

    public SavedRecipeNotFoundException() {
        super();
    }

    public SavedRecipeNotFoundException(String message) {
        super(message);
    }

    public SavedRecipeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SavedRecipeNotFoundException(Throwable cause) {
        super(cause);
    }

}
