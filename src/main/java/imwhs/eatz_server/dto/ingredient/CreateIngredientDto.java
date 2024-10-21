package imwhs.eatz_server.dto.ingredient;

import lombok.Data;

import java.util.List;

@Data
public class CreateIngredientDto {

    private String name;

    private Long categoryId;

    private List<Long> childIds;

}
