package imwhs.eatz_server.dto.eatzuser;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 사용자의 암호 업데이트를 요청하기 위해 필요한 정보를 전달할 때 사용하는 DTO입니다.
 */
@Data
@AllArgsConstructor
public class UpdateEatzUserPasswordRequest {

    /**
     * 사용자 이름
     */
    @NotBlank
    private String username;

    /**
     * 기존 암호
     * <p> 인코딩 되지 않은 암호입니다. </p>
     */
    @NotBlank
    private String existingPassword;

    /**
     * 새 암호
     * <ul> 
     *     <li> 인코딩 되지 않은 암호입니다. </li>
     * </ul>
     */
    @NotBlank
    private String newPassword;

}
