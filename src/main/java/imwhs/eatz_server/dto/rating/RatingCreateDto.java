package imwhs.eatz_server.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingCreateDto {

    int score;

    String content;

}
