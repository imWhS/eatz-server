package imwhs.eatz_server.dto.plan;

import imwhs.eatz_server.domain.Ingredient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChecklistIngredientDto {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private boolean isMissing;

    private boolean isLikedByUser;

    public ChecklistIngredientDto(Long id, String name, boolean isLikedByUser) {
        this.id = id;
        this.name = name;
        this.isLikedByUser = isLikedByUser;
    }

    public ChecklistIngredientDto(Ingredient ingredient, boolean isMissing, boolean isLikedByUser) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();
        this.isMissing = isMissing;
        this.isLikedByUser = isLikedByUser;
    }

}
