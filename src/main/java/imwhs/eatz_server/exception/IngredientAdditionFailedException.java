package imwhs.eatz_server.exception;

public class IngredientAdditionFailedException extends RuntimeException {

    public IngredientAdditionFailedException() {
        super("모든 재료를 추가하지 못했어요.");
    }

}
