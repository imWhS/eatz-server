package imwhs.eatz_server.dto.ingredient;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class IngredientCreateRequest {

    @NotBlank
    private String name;

    private Long parentId;

    private List<Long> childIds = new ArrayList<>();

    public IngredientCreateRequest(String name) {
        this.name = name;
    }

    public IngredientCreateRequest(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public IngredientCreateRequest(String name, List<Long> childIds) {
        this.name = name;
        this.childIds = childIds;
    }

    public IngredientCreateRequest(String name, Long parentId, List<Long> childIds) {
        this.name = name;
        this.parentId = parentId;
        this.childIds = childIds;
    }

}
