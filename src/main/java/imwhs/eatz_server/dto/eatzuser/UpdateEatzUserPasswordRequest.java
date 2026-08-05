package imwhs.eatz_server.dto.eatzuser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(min = 6, max = 20, message = "사용자 이름은 최소 4자부터 최대 20자까지 입력할 수 있어요.")
    @Pattern(regexp = "^[a-z0-9_.]+$", message = "사용자 이름은 알파벳 소문자, 숫자, 밑줄(_), 마침표(.)만 사용할 수 있어요.")
    private String username;

    /**
     * 기존 암호
     * <ul>
     *     <li> 인코딩 되지 않은 암호입니다. </li>
     * </ul>
     */
    @NotBlank
    @Size(min = 8, max = 64, message = "암호는 최소 8자부터 최대 64자까지 입력할 수 있어요.")
    private String existingPassword;

    /**
     * 새 암호
     * <ul> 
     *     <li> 인코딩 되지 않은 암호입니다. </li>
     * </ul>
     */
    @NotBlank
    @Size(min = 8, max = 64, message = "암호는 최소 8자부터 최대 64자까지 입력할 수 있어요.")
    private String newPassword;

}
