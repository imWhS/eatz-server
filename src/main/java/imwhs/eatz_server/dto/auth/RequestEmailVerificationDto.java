package imwhs.eatz_server.dto.auth;

import lombok.Data;

@Data
public class RequestEmailVerificationDto {

    private String email;

    private String code;

}
