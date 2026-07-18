package imwhs.eatz_server.dto.kitchenware;

import imwhs.eatz_server.domain.Kitchenware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 도구의 핵심 정보를 전달할 때 사용합니다.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Data
public class KitchenwareEssentialDto {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String imageUrl;

    public KitchenwareEssentialDto(Kitchenware kitchenware) {
        this.id = kitchenware.getId();
        this.name = kitchenware.getName();
        this.imageUrl = kitchenware.getImageUrl();
    }

}
