package imwhs.eatz_server.dto.ingredient;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UpdateIngredientDto {

    private Long id;

    private String name;

    private Long categoryId;

    private List<Long> childIds = new ArrayList<>();

    public UpdateIngredientDto(Long id, String name) {
        this.name = name;
    }

    public UpdateIngredientDto(Long id, String name, Long categoryId) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
    }

    public UpdateIngredientDto(Long id, String name, List<Long> childIds) {
        this.id = id;
        this.name = name;
        this.childIds = childIds;
    }

    public UpdateIngredientDto(Long id, String name, Long categoryId, List<Long> childIds) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.childIds = childIds;
    }

}
