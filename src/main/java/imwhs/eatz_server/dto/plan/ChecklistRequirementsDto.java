package imwhs.eatz_server.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistRequirementsDto {

    private Set<ChecklistIngredientDto> ingredients;

    private Set<ChecklistKitchenwareDto> kitchenwares;

}
