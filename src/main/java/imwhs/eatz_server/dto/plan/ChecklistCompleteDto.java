package imwhs.eatz_server.dto.plan;

import lombok.Data;

import java.util.List;

@Data
public class ChecklistCompleteDto {

    private List<Long> ingredientIds;

}
