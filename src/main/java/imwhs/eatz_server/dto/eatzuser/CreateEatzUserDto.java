package imwhs.eatz_server.dto.eatzuser;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 사용자 생성 DTO
 */
@Data
@Builder
@AllArgsConstructor
public class CreateEatzUserDto {

    private String username;

    private String email;

    private String password;

    private Role role;

    public EatzUser toEntity() {
        return EatzUser.create(this.username, this.email, this.password, this.role);
    }

}
