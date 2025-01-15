package imwhs.eatz_server.dto.eatzuser;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * EatzUserDeleteDto 클래스입니다.<br/>
 * 특정 사용자의 삭제(회원 탈퇴)에 필요한 정보를 전달하기 위한 DTO입니다.
 */
@Data
@AllArgsConstructor
public class EatzUserDeleteDto {

    private String existingPassword;

}
