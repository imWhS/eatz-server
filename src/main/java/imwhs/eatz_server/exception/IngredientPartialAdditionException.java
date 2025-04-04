package imwhs.eatz_server.exception;

public class IngredientPartialAdditionException extends RuntimeException {

    public IngredientPartialAdditionException() {
        super("일부 재료만 추가됐어요.");
    }

}
