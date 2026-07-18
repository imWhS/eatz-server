package imwhs.eatz_server.dto.kitchenware;

import imwhs.eatz_server.domain.Kitchenware;
import imwhs.eatz_server.dto.CreationInfoResponse;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class KitchenwareCreationInfoResponse extends CreationInfoResponse {

    public KitchenwareCreationInfoResponse(Long id, LocalDateTime createdAt) {
        super(id, createdAt);
    }

    public KitchenwareCreationInfoResponse(Kitchenware kitchenware) {
        this(kitchenware.getId(), kitchenware.getCreatedAt());
    }

}
