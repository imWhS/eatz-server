package imwhs.eatz_server.dto.eatzuser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateEatzUserUsernameRequest {

    @NotBlank
    @Size(min = 4, max = 20, message = "사용자 이름은 최소 4자부터 최대 20자까지 입력할 수 있어요.")
    @Pattern(regexp = "^[a-z0-9_.]+$", message = "사용자 이름은 알파벳 소문자, 숫자, 밑줄(_), 마침표(.)만 사용할 수 있어요.")
    private String username;

}
