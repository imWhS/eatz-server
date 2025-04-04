package imwhs.eatz_server.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenExceptionOld extends AuthenticationException {

    public InvalidTokenExceptionOld(String message) {
        super(message);
    }

}
