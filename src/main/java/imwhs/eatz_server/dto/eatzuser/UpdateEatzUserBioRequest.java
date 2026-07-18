package imwhs.eatz_server.dto.eatzuser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateEatzUserBioRequest {

    @NotBlank
    @Size(max = 150, message = "소개는 150자까지 입력할 수 있어요.")
    private String bio;

}
