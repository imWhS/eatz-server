package imwhs.eatz_server.dto.kitchenware;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KitchenwareCreateRequest {

    @NotBlank
    private String name;

    public KitchenwareCreateRequest(String name) {
        this.name = name;
    }

}
