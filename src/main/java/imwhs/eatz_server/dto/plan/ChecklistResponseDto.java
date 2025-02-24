package imwhs.eatz_server.dto.plan;

import imwhs.eatz_server.dto.ingredient.IngredientDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class ChecklistResponseDto {

    private final List<Long> cookable;

    private final List<Long> uncookable;

    private final Set<IngredientDto> missingIngredients;

}
