package imwhs.eatz_server.dto.eatzuser;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * EatzUserWithRecipeSummaryDto 클래스입니다.
 * <p>
 *     사용자 및 사용자가 등록한 레시피 수 정보를 전달하기 위한 DTO로 사용합니다.
 * </p>
 */
@Data
@AllArgsConstructor
public class EatzUserWithRecipeSummaryDto {

    private Long id;

    private String username;

    private String imageUrl;

    private Integer recipeCount;

}
