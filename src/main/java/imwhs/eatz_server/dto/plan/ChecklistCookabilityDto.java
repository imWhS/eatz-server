package imwhs.eatz_server.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChecklistCookabilityDto {

    private List<PlanBasicDto> plans;

    private ChecklistRequirementsDto requirements;

}
