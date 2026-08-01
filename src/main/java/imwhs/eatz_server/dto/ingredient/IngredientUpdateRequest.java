package imwhs.eatz_server.dto.ingredient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class IngredientUpdateRequest {

    @NotBlank
    private String name;

    private Long parentId;

    @NotNull
    private Boolean isParentCoupled;

    private List<Long> childIds = new ArrayList<>();

    public IngredientUpdateRequest(String name) {
        this.name = name;
    }

    public IngredientUpdateRequest(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public IngredientUpdateRequest(String name, List<Long> childIds) {
        this.name = name;
        this.childIds = childIds;
    }

    public IngredientUpdateRequest(String name, Long parentId, List<Long> childIds) {
        this.name = name;
        this.parentId = parentId;
        this.childIds = childIds;
    }

    public IngredientUpdateRequest(String name, Long parentId, Boolean isParentCoupled, List<Long> childIds) {
        this.name = name;
        this.parentId = parentId;
        this.isParentCoupled = isParentCoupled;
        this.childIds = childIds;
    }

}
