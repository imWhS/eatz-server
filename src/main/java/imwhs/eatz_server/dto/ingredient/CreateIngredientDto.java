package imwhs.eatz_server.dto.ingredient;

import lombok.Data;

import java.util.List;

@Data
public class CreateIngredientDto {

    private String name;

    private Long parentId;

    private List<Long> childIds;

}
