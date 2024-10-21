package imwhs.eatz_server.dto.ingredient;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateIngredientDto {

    private String name;

    private Long categoryId;

    private List<Long> childIds;

    public CreateIngredientDto(String name) {
        this.name = name;
    }

    public CreateIngredientDto(String name, Long categoryId) {
        this.name = name;
        this.categoryId = categoryId;
    }

    public CreateIngredientDto(String name, Long categoryId, List<Long> childIds) {
        this.name = name;
        this.categoryId = categoryId;
        this.childIds = childIds;
    }

}
