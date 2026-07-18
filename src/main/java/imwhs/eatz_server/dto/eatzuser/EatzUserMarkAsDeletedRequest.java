package imwhs.eatz_server.dto.eatzuser;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자의 삭제(회원 탈퇴)를 요청하기 위해 필요한 정보를 전달할 때 사용하는 DTO입니다.
 */
@Data
@NoArgsConstructor

@AllArgsConstructor
public class EatzUserMarkAsDeletedRequest {

    @NotBlank
    private String existingPassword;

}
