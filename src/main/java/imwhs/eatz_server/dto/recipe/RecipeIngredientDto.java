package imwhs.eatz_server.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
public class RecipeIngredientDto {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private Boolean ownedByUser;

    public RecipeIngredientDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
