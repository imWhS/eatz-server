package imwhs.eatz_server.dto.ingredient;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IngredientWithOwnedStatusDto {

    Long ingredientId;

    boolean ownedByUser;

}
