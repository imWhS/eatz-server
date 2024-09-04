package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateEatzUserDto {

    private String username;

    private String email;

    private String password;

    private Role role;

}
