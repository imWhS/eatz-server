package imwhs.eatz_server.dto.eatzuser;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Role;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * EatzUserDto 클래스입니다.<br/>
 * 사용자 정보를 전달하기 위해 사용합니다.
 */
@Data
public class EatzUserDto {

    private Long id;

    private String username;

    private String email;

    private Role role;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    public EatzUserDto(EatzUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.deletedAt = user.getDeletedAt();
    }

}
