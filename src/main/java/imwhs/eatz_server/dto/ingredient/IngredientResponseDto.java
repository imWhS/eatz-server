package imwhs.eatz_server.dto.ingredient;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class IngredientResponseDto {

    private Long id;

    private String name;

    private List<Long> childIds;

    private List<String> childNames;

    private IngredientCategoryResponseDto category;

    private List<IngredientChildResponseDto> children;

}
