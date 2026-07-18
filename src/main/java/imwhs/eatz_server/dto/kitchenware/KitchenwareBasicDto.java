package imwhs.eatz_server.dto.kitchenware;

import imwhs.eatz_server.domain.Kitchenware;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 도구(Kitchenware)의 기본 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> Kitchenware의 핵심 및 대부분의 정보와 현재 로그인 사용자의 context를 포함합니다. </li>
 *      <li> 주로 컬렉션으로 조회할 때 사용합니다. </li>
 * </ul>
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Data
public class KitchenwareBasicDto {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String imageUrl;

    private boolean isOwnedByUser;

    public KitchenwareBasicDto(Long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public KitchenwareBasicDto(Kitchenware kitchenware) {
        this.id = kitchenware.getId();
        this.name = kitchenware.getName();
        this.imageUrl = kitchenware.getImageUrl();
    }

}
