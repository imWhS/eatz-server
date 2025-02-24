package imwhs.eatz_server.dto.ingredient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class IngredientDto {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

}
