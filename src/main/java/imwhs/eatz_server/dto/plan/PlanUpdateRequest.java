package imwhs.eatz_server.dto.plan;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PlanUpdateRequest {

    @NotNull
    LocalDateTime date;

    @NotNull
    Integer priority;

}
