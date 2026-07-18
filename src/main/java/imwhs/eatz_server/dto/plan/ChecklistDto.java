package imwhs.eatz_server.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChecklistDto {

    private final ChecklistCookabilityDto uncookable;

    private final ChecklistCookabilityDto cookable;

    private final long missingIngredientCount;

    private final long missingKitchenwareCount;

}
