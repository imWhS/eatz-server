package imwhs.eatz_server.dto.plan;

import lombok.Data;

import java.util.List;

@Data
public class CompleteChecklistDto {

    private List<Long> ingredientIds;

}
