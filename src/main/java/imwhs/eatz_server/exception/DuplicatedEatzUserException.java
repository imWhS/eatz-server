package imwhs.eatz_server.exception;

public class DuplicatedEatzUserException extends RuntimeException {

    public DuplicatedEatzUserException() {
        super();
    }

    public DuplicatedEatzUserException(String message) {
        super(message);
    }

    public DuplicatedEatzUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedEatzUserException(Throwable cause) {
        super(cause);
    }


}
