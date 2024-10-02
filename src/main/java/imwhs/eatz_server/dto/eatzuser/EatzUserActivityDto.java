package imwhs.eatz_server.dto.eatzuser;

import imwhs.eatz_server.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EatzUserActivityDto {

    private Long id;

    private String username;

    private Long recipeCount;

//    private Long commentCount;

//    private Long ratingCount;

}
