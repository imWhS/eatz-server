package imwhs.eatz_server.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PlannedDatesDto {

    private List<LocalDateTime> plannedDates;

}
