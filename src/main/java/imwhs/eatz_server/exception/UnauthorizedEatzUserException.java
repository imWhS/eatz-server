package imwhs.eatz_server.exception;

public class UnauthorizedEatzUserException extends RuntimeException {

    public UnauthorizedEatzUserException() {
        super();
    }

    public UnauthorizedEatzUserException(String message) {
        super(message);
    }

    public UnauthorizedEatzUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedEatzUserException(Throwable cause) {
        super(cause);
    }

}
