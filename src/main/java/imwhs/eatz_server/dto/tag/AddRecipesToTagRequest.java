package imwhs.eatz_server.dto.tag;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddRecipesToTagRequest {

    @NotEmpty
    private List<Long> recipeIds;

}
