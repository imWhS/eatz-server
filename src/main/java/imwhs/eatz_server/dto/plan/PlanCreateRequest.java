package imwhs.eatz_server.dto.plan;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanCreateRequest {

    @NotNull
    Long recipeId;

    @NotNull
    LocalDateTime date;

    @NotNull
    Integer priority;

}
