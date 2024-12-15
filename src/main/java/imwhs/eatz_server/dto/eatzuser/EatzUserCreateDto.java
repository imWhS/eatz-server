package imwhs.eatz_server.dto.eatzuser;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EatzUserCreateDto 클래스입니다.<br/>
 * 사용자 생성에 필요한 정보를 전달하기 위해 사용합니다.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EatzUserCreateDto {

    @NotBlank(message = "사용자 이름은 필수 항목입니다.")
    private String username;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "이메일 주소 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀 번호는 필수 항목입니다.")
    private String password;

    /**
     * 회원 역할을 가지는 사용자 엔티티를 생성합니다.
     * @return EatzUser 사용자 엔티티.
     */
    public EatzUser toMemberEntity() {
        return EatzUser.create(this.username, this.email, this.password, Role.MEMBER);
    }

}
