package imwhs.eatz_server.exception.token;

import org.springframework.security.core.AuthenticationException;

public class MissingTokenException extends AuthenticationException {

    public MissingTokenException(String message) {
        super(message);
    }

}
