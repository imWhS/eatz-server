package imwhs.eatz_server.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RecipeSummaryDto 클래스입니다.
 * 특정 레시피에 대한 간략한 정보를 전달하기 위해 사용합니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeSummaryDto {

    private Long id;

    private String title;

    private String imageUrl;

}
