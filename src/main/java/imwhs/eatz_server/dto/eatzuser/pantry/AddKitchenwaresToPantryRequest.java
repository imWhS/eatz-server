package imwhs.eatz_server.dto.eatzuser.pantry;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddKitchenwaresToPantryRequest {

    @NotNull
    List<Long> kitchenwareIds;

}
