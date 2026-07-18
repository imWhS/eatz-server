package imwhs.eatz_server.dto.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.dto.CreationInfoResponse;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class IngredientCreationInfoResponse extends CreationInfoResponse {

    public IngredientCreationInfoResponse(Long id, LocalDateTime createdAt) {
        super(id, createdAt);
    }

    public IngredientCreationInfoResponse(Ingredient ingredient) {
        this(ingredient.getId(), ingredient.getCreatedAt());
    }

}
