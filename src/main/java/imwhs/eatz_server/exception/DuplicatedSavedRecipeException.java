package imwhs.eatz_server.exception;

public class DuplicatedSavedRecipeException extends RuntimeException {

    public DuplicatedSavedRecipeException() {
        super();
    }

    public DuplicatedSavedRecipeException(String message) {
        super(message);
    }

    public DuplicatedSavedRecipeException(Long id, String username) {
        super(String.format("사용자(%d)가 이미 레시피(%s)를 저장했어요.", id, username));
    }

    public DuplicatedSavedRecipeException(Throwable cause) {
        super(cause);
    }


}
