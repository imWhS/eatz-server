package imwhs.eatz_server.dto.eatzuser;

import lombok.Data;

/**
 * EatzUserUpdateDto 클래스입니다.<br/>
 * 특정 사용자에 대해 업데이트가 필요한 정보를 전달하기 위한 DTO입니다.
 */
@Data
public class EatzUserUpdateDto {

    private String username;

    private String email;

    private String password;

}
