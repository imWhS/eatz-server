package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IngredientChildResponseDto {

    private Long childId;

    private String childName;

    public IngredientChildResponseDto(Ingredient child) {
        this.childId = child.getId();
        this.childName = child.getName();
    }

}