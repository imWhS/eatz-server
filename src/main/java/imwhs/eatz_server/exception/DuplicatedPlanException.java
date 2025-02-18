package imwhs.eatz_server.exception;

public class DuplicatedPlanException extends RuntimeException {

    public DuplicatedPlanException() {
        super();
    }

    public DuplicatedPlanException(String message) {
        super(message);
    }

    public DuplicatedPlanException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedPlanException(Throwable cause) {
        super(cause);
    }

}
