package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.domain.EatzUser;
import lombok.AllArgsConstructor;
import lombok.Data;

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
