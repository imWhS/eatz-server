package imwhs.eatz_server.dto.eatzuser.pantry;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class PantryAdditionInfoResponse {

    private List<Long> addedIds;

}
