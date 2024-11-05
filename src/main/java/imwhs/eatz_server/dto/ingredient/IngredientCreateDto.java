package imwhs.eatz_server.dto.ingredient;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class IngredientCreateDto {

    private String name;

    private Long categoryId;

    private List<Long> childIds = new ArrayList<>();

    public IngredientCreateDto(String name) {
        this.name = name;
    }

    public IngredientCreateDto(String name, Long categoryId) {
        this.name = name;
        this.categoryId = categoryId;
    }

    public IngredientCreateDto(String name, List<Long> childIds) {
        this.name = name;
        this.childIds = childIds;
    }

    public IngredientCreateDto(String name, Long categoryId, List<Long> childIds) {
        this.name = name;
        this.categoryId = categoryId;
        this.childIds = childIds;
    }

}
