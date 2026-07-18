package imwhs.eatz_server.common.error;

import org.springframework.http.HttpStatus;

public interface EatzErrorType {

    HttpStatus getStatus();
    String getCode();
    String getMessage();

}
