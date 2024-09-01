package imwhs.eatz_server.dto;

import lombok.Data;

@Data
public class UpdateEatzUserDto {

    private String username;

    private String email;

    private String password;

}
