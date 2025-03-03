package imwhs.eatz_server.dto.plan;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CreatePlanDto {

    @NotNull
    Long recipeId;

    @NotNull
    LocalDate date;

    Integer priority;

}
