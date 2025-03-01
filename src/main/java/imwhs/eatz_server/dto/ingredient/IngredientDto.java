package imwhs.eatz_server.dto.ingredient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class IngredientDto {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    public IngredientDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
