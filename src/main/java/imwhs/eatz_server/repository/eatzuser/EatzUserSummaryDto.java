package imwhs.eatz_server.repository.eatzuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EatzUserSummaryDto {

    private Long id;

    private String username;

    private Long recipeCount;

}
