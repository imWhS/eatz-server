package imwhs.eatz_server.dto.eatzuser;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * EatzUserUpdateDto 클래스입니다.<br/>
 * 특정 사용자에 대해 업데이트가 필요한 정보를 전달하기 위한 DTO입니다.
 */
@Data
@AllArgsConstructor
public class EatzUserUpdateDto {

    private String username;

    /**
     * 기존 비밀 번호. Encoding 되지 않은 비밀 번호입니다.
     */
    private String existingPassword;

    /**
     * 새 비밀 번호. Encoding 되지 않은 비밀 번호입니다. null일 경우 기존 비밀 번호를 계속 사용합니다.
     */
    private String newPassword;

}
