package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingWithUserResponseDto {

    private Long id;

    private EatzUserBasicDto user;

    private Integer score;

    private String content;

}
