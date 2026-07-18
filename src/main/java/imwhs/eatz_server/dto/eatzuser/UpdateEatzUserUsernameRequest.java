package imwhs.eatz_server.dto.eatzuser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateEatzUserUsernameRequest {

    @NotBlank
    @Size(max = 20, message = "사용자 이름은 20자까지 입력할 수 있어요.")
    private String username;

}
