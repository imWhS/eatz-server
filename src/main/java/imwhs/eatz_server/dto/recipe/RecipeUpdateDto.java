package imwhs.eatz_server.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * RecipeUpdateDto 클래스입니다.<br/>
 * 레시피에 대해 업데이트가 필요한 정보를 전달하기 위한 DTO입니다.
 */
@Data
@AllArgsConstructor
public class RecipeUpdateDto {

    private String title;

    private String url;

    private String imageUrl;

    private String description;

}
