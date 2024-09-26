package imwhs.eatz_server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.domain.EatzUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.catalina.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RatingUserDto {

    private Long id;

    private String username;

    public RatingUserDto(EatzUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

}
