package imwhs.eatz_server.dto.plan;

import imwhs.eatz_server.domain.Kitchenware;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChecklistKitchenwareDto {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String imageUrl;

    private boolean isMissing;

    public ChecklistKitchenwareDto(Kitchenware kitchenware, boolean isMissing) {
        this.id = kitchenware.getId();
        this.name = kitchenware.getName();
        this.imageUrl = kitchenware.getImageUrl();
        this.isMissing = isMissing;
    }

}
