package imwhs.eatz_server.dto.eatzuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EatzUserSummaryDto 클래스입니다.
 * 특정 사용자에 대한 간략한 정보를 전달하기 위해 사용합니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EatzUserSummaryDto {

    private Long id;

    private String username;

    private Integer recipeCount;

}
