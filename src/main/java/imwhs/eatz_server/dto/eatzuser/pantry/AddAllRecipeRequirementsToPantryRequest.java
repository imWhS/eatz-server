package imwhs.eatz_server.dto.eatzuser.pantry;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddAllRecipeRequirementsToPantryRequest {

    @NotNull
    Long recipeId;

}
