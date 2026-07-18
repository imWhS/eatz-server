package imwhs.eatz_server.dto.eatzuser;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.eatzuser.EatzUserRole;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 사용자(EatzUser)의 상세한 정보를 전달할 때 사용하는 DTO입니다.<br/>
 */
@Data
public class EatzUserDetailDto {

    private Long id;
    private String username;
    private String email;
    private EatzUserRole role;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public EatzUserDetailDto(EatzUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getEatzUserRole();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.deletedAt = user.getDeletedAt();
        this.imageUrl = user.getImageUrl();
    }

}
