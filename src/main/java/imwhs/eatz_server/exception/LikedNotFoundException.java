package imwhs.eatz_server.exception;

public class LikedNotFoundException extends RuntimeException {

    public LikedNotFoundException() {
        super();
    }

    public LikedNotFoundException(String message) {
        super(message);
    }

    public LikedNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LikedNotFoundException(Throwable cause) {
        super(cause);
    }

}
