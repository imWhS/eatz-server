package imwhs.eatz_server.dto.eatzuser.pantry;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AddIngredientsToPantryRequest {

    @NotEmpty
    List<Long> ingredientIds;

}
