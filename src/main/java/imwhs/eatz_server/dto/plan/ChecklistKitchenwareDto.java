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

    /**
     * 도구의 ID
     */
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * 도구의 이름
     */
    private String name;

    /**
     * 도구의 이미지 URL
     */
    private String imageUrl;

    /**
     * 도구가 보관함에 추가되어 있지 않은지 여부
     */
    private boolean isMissing;

    public ChecklistKitchenwareDto(Kitchenware kitchenware, boolean isMissing) {
        this.id = kitchenware.getId();
        this.name = kitchenware.getName();
        this.imageUrl = kitchenware.getImageUrl();
        this.isMissing = isMissing;
    }

}
