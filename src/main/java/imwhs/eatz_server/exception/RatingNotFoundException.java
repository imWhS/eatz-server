package imwhs.eatz_server.exception;

public class RatingNotFoundException extends RuntimeException {

    public RatingNotFoundException() {
        super();
    }

    public RatingNotFoundException(String message) {
        super(message);
    }

    public RatingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RatingNotFoundException(Throwable cause) {
        super(cause);
    }

}
