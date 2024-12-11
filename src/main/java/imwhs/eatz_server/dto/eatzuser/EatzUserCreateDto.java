package imwhs.eatz_server.dto.eatzuser;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * EatzUserCreateDto 클래스입니다.<br/>
 * 사용자 생성에 필요한 정보를 전달하기 위해 사용합니다.
 */
@Data
@Builder
@AllArgsConstructor
public class EatzUserCreateDto {

    private String username;

    private String email;

    private String password;

    private Role role;

    public EatzUser toEntity() {
        return EatzUser.create(this.username, this.email, this.password, this.role);
    }

}
