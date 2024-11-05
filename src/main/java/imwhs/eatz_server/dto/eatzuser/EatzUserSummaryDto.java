package imwhs.eatz_server.dto.eatzuser;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * EatzUserSummaryDto 클래스입니다.
 * <ul>
 *     <li>사용자 및 해당 사용자의 부가 정보를 포함하는 DTO입니다.</li>
 *     <li>사용자가 등록한 레시피 수가 사용자의 부가 정보로서 제공됩니다.</li>
 * </ul>
 *
 *
 */
@Data
@AllArgsConstructor
public class EatzUserSummaryDto {

    private Long id;

    private String username;

    private Integer recipeCount;

}
