package imwhs.eatz_server.dto.plan;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ChecklistCompletionRequest {

    @NotEmpty
    private List<Long> ingredientIds;

}
