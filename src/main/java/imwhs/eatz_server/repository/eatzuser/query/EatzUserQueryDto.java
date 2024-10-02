package imwhs.eatz_server.repository.eatzuser.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EatzUserQueryDto {

    private Long id;

    private String username;

    private String email;

    private Role role;

    private List<EatzUserRecipeQueryDto> recipeList;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    public EatzUserQueryDto(Long id, String username, String email, Role role, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

}
