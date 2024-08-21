package imwhs.eatz_server.exception;

public class EatzUserNotFoundException extends RuntimeException {

    public EatzUserNotFoundException() {
        super();
    }

    public EatzUserNotFoundException(String message) {
        super(message);
    }

    public EatzUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EatzUserNotFoundException(Throwable cause) {
        super(cause);
    }

}
