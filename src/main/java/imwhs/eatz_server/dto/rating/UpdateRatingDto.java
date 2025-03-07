package imwhs.eatz_server.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateRatingDto {

    int score;

    String content;

}
