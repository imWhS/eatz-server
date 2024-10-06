package imwhs.eatz_server.dto.eatzuser;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EatzUserActivityResponseDto {

    private Long id;

    private String username;

    private Long recipeCount;

//    private Long commentCount;

//    private Long ratingCount;

}
